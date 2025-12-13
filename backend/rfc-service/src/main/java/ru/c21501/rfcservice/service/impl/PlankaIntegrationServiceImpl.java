package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.client.PlankaClient;
import ru.c21501.rfcservice.dto.planka.PlankaCardRequest;
import ru.c21501.rfcservice.dto.planka.PlankaCardResponse;
import ru.c21501.rfcservice.dto.planka.PlankaWebhookPayload;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.openapi.model.RfcStatus;
import ru.c21501.rfcservice.openapi.model.Urgency;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.service.PlankaIntegrationService;

import java.util.*;

/**
 * Реализация сервиса интеграции с Planka
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlankaIntegrationServiceImpl implements PlankaIntegrationService {

    private final PlankaClient plankaClient;
    private final RfcRepository rfcRepository;

    @Value("${planka.webhook-secret:}")
    private String expectedWebhookSecret;

    @Value("${planka.board-id:}")
    private String defaultBoardId;

    @Value("${planka.enabled:false}")
    private boolean plankaEnabled;

    /**
     * Маппинг статусов RFC на названия списков в Planka
     */
    private static final Map<RfcStatus, List<String>> STATUS_TO_LIST_NAMES = Map.of(
            RfcStatus.NEW, List.of("Новый", "Новые", "New", "Новые запросы", "New Requests", "Backlog"),
            RfcStatus.UNDER_REVIEW, List.of("На рассмотрении", "Under Review", "Review", "In Review"),
            RfcStatus.APPROVED, List.of("Одобрен", "Утверждено", "Approved", "Ready"),
            RfcStatus.IMPLEMENTED, List.of("Внедрен", "Выполнено", "Implemented", "Done", "Completed"),
            RfcStatus.REJECTED, List.of("Отклонен", "Отклонено", "Rejected", "Cancelled")
    );

    /**
     * Обратный маппинг: название списка -> статус RFC
     */
    private static final Map<String, RfcStatus> LIST_NAME_TO_STATUS;

    static {
        Map<String, RfcStatus> map = new HashMap<>();
        STATUS_TO_LIST_NAMES.forEach((status, names) -> 
            names.forEach(name -> map.put(name.toLowerCase(), status))
        );
        LIST_NAME_TO_STATUS = Collections.unmodifiableMap(map);
    }

    @Override
    public void handlePlankaWebhook(PlankaWebhookPayload payload, String webhookSecret) {
        validateWebhookSecret(webhookSecret);
        
        if (!plankaEnabled) {
            log.debug("Planka integration is disabled, skipping webhook");
            return;
        }

        String event = payload.getEvent();
        log.info("Processing Planka webhook: event={}", event);

        switch (event) {
            case "card_created" -> handleCardCreated(payload);
            case "card_updated" -> handleCardUpdated(payload);
            case "card_moved" -> handleCardMoved(payload);
            case "card_deleted" -> handleCardDeleted(payload);
            case "rfc_status_changed" -> handleRfcStatusChanged(payload);
            default -> log.warn("Unknown webhook event: {}", event);
        }
    }

    @Override
    public void handleCardMovedWebhook(PlankaWebhookPayload payload, String webhookSecret) {
        validateWebhookSecret(webhookSecret);
        
        if (!plankaEnabled) return;

        handleCardMoved(payload);
    }

    @Override
    public void handleCardUpdatedWebhook(PlankaWebhookPayload payload, String webhookSecret) {
        validateWebhookSecret(webhookSecret);
        
        if (!plankaEnabled) return;

        handleCardUpdated(payload);
    }

    @Override
    @Transactional
    public void syncRfcToPlanka(RfcEntity rfc) {
        if (!plankaEnabled) {
            log.debug("Planka integration is disabled, skipping sync");
            return;
        }

        log.info("Syncing RFC to Planka: rfcId={}", rfc.getId());

        // Если карточка уже существует - обновляем
        if (rfc.getPlankaCardId() != null) {
            updatePlankaCardForRfc(rfc, rfc.getPlankaCardId());
            updateRfcStatusInPlanka(rfc, rfc.getPlankaCardId());
            return;
        }

        // Создаём новую карточку
        String plankaCardId = createPlankaCardForRfc(rfc);
        
        if (plankaCardId != null) {
            log.info("RFC synced to Planka: rfcId={}, plankaCardId={}", rfc.getId(), plankaCardId);
            // Сохраняем plankaCardId в RfcEntity
            rfc.setPlankaCardId(plankaCardId);
            rfcRepository.save(rfc);
        }
    }

    @Override
    public String createPlankaCardForRfc(RfcEntity rfc) {
        if (!plankaEnabled || defaultBoardId.isBlank()) {
            log.warn("Planka integration is disabled or board ID not configured");
            return null;
        }

        // Найти список для текущего статуса RFC
        String listId = findListIdForStatus(rfc.getStatus());
        if (listId == null) {
            log.error("Could not find list for status: {}", rfc.getStatus());
            return null;
        }

        PlankaCardRequest request = buildCardRequest(rfc);
        Optional<PlankaCardResponse> response = plankaClient.createCard(listId, request);
        
        return response.map(PlankaCardResponse::getId).orElse(null);
    }

    @Override
    public void updatePlankaCardForRfc(RfcEntity rfc, String plankaCardId) {
        if (!plankaEnabled) return;

        PlankaCardRequest request = buildCardRequest(rfc);
        plankaClient.updateCard(plankaCardId, request);
    }

    @Override
    public void updateRfcStatusInPlanka(RfcEntity rfc, String plankaCardId) {
        if (!plankaEnabled) return;

        String targetListId = findListIdForStatus(rfc.getStatus());
        if (targetListId != null) {
            plankaClient.moveCard(plankaCardId, targetListId, null);
        }
    }

    @Override
    public void deletePlankaCard(String plankaCardId) {
        if (!plankaEnabled) return;

        plankaClient.deleteCard(plankaCardId);
    }

    // ========== Private methods ==========

    private void validateWebhookSecret(String webhookSecret) {
        if (expectedWebhookSecret != null && !expectedWebhookSecret.isBlank()) {
            if (!expectedWebhookSecret.equals(webhookSecret)) {
                throw new SecurityException("Invalid webhook secret");
            }
        }
    }

    @Transactional
    private void handleCardCreated(PlankaWebhookPayload payload) {
        var data = payload.getData();
        if (data == null || data.getRfcData() == null) {
            log.debug("Card created event without RFC data, skipping");
            return;
        }

        // Если карточка создана с external RFC ID, связываем
        Long externalRfcId = data.getRfcData().getExternalRfcId();
        if (externalRfcId != null) {
            log.info("Card created for existing RFC: externalRfcId={}", externalRfcId);
            // TODO: Связать plankaCardId с RfcEntity
        } else {
            // Создаём новый RFC из карточки Planka
            log.info("Creating new RFC from Planka card: cardId={}", data.getCardId());
            // TODO: Создать RFC из данных карточки
        }
    }

    @Transactional
    private void handleCardUpdated(PlankaWebhookPayload payload) {
        var data = payload.getData();
        if (data == null || data.getRfcData() == null) return;

        Long externalRfcId = data.getRfcData().getExternalRfcId();
        if (externalRfcId == null) return;

        Optional<RfcEntity> rfcOpt = rfcRepository.findById(externalRfcId);
        if (rfcOpt.isEmpty()) {
            log.warn("RFC not found for update: id={}", externalRfcId);
            return;
        }

        RfcEntity rfc = rfcOpt.get();
        
        // Обновляем поля RFC
        if (data.getName() != null) {
            rfc.setTitle(data.getName());
        }
        if (data.getDescription() != null) {
            rfc.setDescription(data.getDescription());
        }
        
        var rfcData = data.getRfcData();
        if (rfcData.getUrgency() != null) {
            try {
                rfc.setUrgency(Urgency.valueOf(rfcData.getUrgency()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid urgency value: {}", rfcData.getUrgency());
            }
        }
        if (rfcData.getImplementationDate() != null) {
            rfc.setImplementationDate(rfcData.getImplementationDate());
        }

        rfcRepository.save(rfc);
        log.info("RFC updated from Planka: rfcId={}", rfc.getId());
    }

    @Transactional
    private void handleCardMoved(PlankaWebhookPayload payload) {
        var data = payload.getData();
        if (data == null) return;

        // Определяем новый статус по названию списка
        String newListName = data.getListName();
        if (newListName == null) {
            log.debug("Card moved without list name");
            return;
        }

        RfcStatus newStatus = LIST_NAME_TO_STATUS.get(newListName.toLowerCase());
        if (newStatus == null) {
            log.debug("Unknown list name, cannot determine status: {}", newListName);
            return;
        }

        // Сначала пробуем найти RFC по plankaCardId
        String cardId = data.getCardId();
        Optional<RfcEntity> rfcOpt = Optional.empty();
        
        if (cardId != null) {
            rfcOpt = rfcRepository.findByPlankaCardId(cardId);
            log.debug("Looking for RFC by plankaCardId: {}, found: {}", cardId, rfcOpt.isPresent());
        }
        
        // Если не нашли по cardId, пробуем по externalRfcId
        if (rfcOpt.isEmpty() && data.getRfcData() != null && data.getRfcData().getExternalRfcId() != null) {
            Long externalRfcId = data.getRfcData().getExternalRfcId();
            rfcOpt = rfcRepository.findById(externalRfcId);
            log.debug("Looking for RFC by externalRfcId: {}, found: {}", externalRfcId, rfcOpt.isPresent());
        }

        if (rfcOpt.isEmpty()) {
            log.warn("RFC not found for card move: cardId={}", cardId);
            return;
        }

        RfcEntity rfc = rfcOpt.get();
        RfcStatus oldStatus = rfc.getStatus();

        if (oldStatus != newStatus) {
            // Получаем информацию о пользователе из webhook
            var movedBy = data.getMovedBy();
            String userName = movedBy != null ? movedBy.getName() : "Unknown";
            String userUsername = movedBy != null ? movedBy.getUsername() : "unknown";
            
            log.info("=== RFC STATUS CHANGE FROM PLANKA ===");
            log.info("RFC ID: {}", rfc.getId());
            log.info("RFC Title: {}", rfc.getTitle());
            log.info("Status Change: {} -> {}", oldStatus, newStatus);
            log.info("Changed By (Planka User): {} ({})", userName, userUsername);
            log.info("Source: Planka card move");
            log.info("=====================================");
            
            rfc.setStatus(newStatus);
            rfcRepository.save(rfc);
            
            log.info("RFC {} status successfully updated to {} by Planka user: {}", 
                    rfc.getId(), newStatus, userName);
        }
    }

    private void handleCardDeleted(PlankaWebhookPayload payload) {
        var data = payload.getData();
        if (data == null || data.getRfcData() == null) return;

        Long externalRfcId = data.getRfcData().getExternalRfcId();
        if (externalRfcId != null) {
            log.info("Planka card deleted for RFC: rfcId={}", externalRfcId);
            // TODO: Решить что делать - удалять RFC или просто отвязывать
        }
    }

    private void handleRfcStatusChanged(PlankaWebhookPayload payload) {
        // Аналогично handleCardMoved
        handleCardMoved(payload);
    }

    private String findListIdForStatus(RfcStatus status) {
        if (defaultBoardId == null || defaultBoardId.isBlank()) return null;

        List<String> possibleNames = STATUS_TO_LIST_NAMES.getOrDefault(status, List.of());
        
        for (String name : possibleNames) {
            Optional<String> listId = plankaClient.findListIdByName(defaultBoardId, name);
            if (listId.isPresent()) {
                return listId.get();
            }
        }
        
        log.warn("Could not find list for status {} in board {}", status, defaultBoardId);
        return null;
    }

    private PlankaCardRequest buildCardRequest(RfcEntity rfc) {
        // Формируем описание с RFC метаданными
        StringBuilder descBuilder = new StringBuilder();
        if (rfc.getDescription() != null) {
            descBuilder.append(rfc.getDescription()).append("\n\n");
        }
        descBuilder.append("---\n");
        descBuilder.append("**RFC ID:** ").append(rfc.getId()).append("\n");
        descBuilder.append("**Статус:** ").append(rfc.getStatus().name()).append("\n");
        descBuilder.append("**Срочность:** ").append(rfc.getUrgency().name()).append("\n");
        descBuilder.append("**Дата внедрения:** ").append(rfc.getImplementationDate()).append("\n");
        descBuilder.append("**Создатель:** ").append(rfc.getRequester().getFullName()).append("\n");
        
        // Добавляем затронутые системы
        descBuilder.append("\n**Затронутые системы:**\n");
        rfc.getAffectedSubsystems().forEach(affected -> {
            descBuilder.append("- ").append(affected.getSubsystem().getSystem().getName())
                    .append(" / ").append(affected.getSubsystem().getName());
            if (affected.getExecutor() != null) {
                descBuilder.append(" (").append(affected.getExecutor().getFullName()).append(")");
            }
            descBuilder.append("\n");
        });

        return PlankaCardRequest.builder()
                .name(rfc.getTitle())
                .description(descBuilder.toString())
                .position(65536.0) // Стандартная позиция Planka
                .type("project") // Тип карточки в Planka (project или story)
                .build();
    }
}

