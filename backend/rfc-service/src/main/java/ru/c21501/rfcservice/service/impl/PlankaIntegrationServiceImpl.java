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
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.model.entity.RfcHistoryEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;
import ru.c21501.rfcservice.openapi.model.RfcStatus;
import ru.c21501.rfcservice.openapi.model.Urgency;
import ru.c21501.rfcservice.repository.RfcHistoryRepository;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.repository.UserRepository;
import ru.c21501.rfcservice.service.PlankaIntegrationService;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса интеграции с Planka
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlankaIntegrationServiceImpl implements PlankaIntegrationService {

    private final PlankaClient plankaClient;
    private final RfcRepository rfcRepository;
    private final RfcHistoryRepository rfcHistoryRepository;
    private final UserRepository userRepository;

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
            case "cardCreate", "card_created" -> handleCardCreated(payload);
            case "cardUpdate", "card_updated" -> handleCardUpdated(payload);
            case "cardMove", "card_moved" -> handleCardMoved(payload);
            case "cardDelete", "card_deleted" -> handleCardDeleted(payload);
            case "rfc_status_changed" -> handleRfcStatusChanged(payload);
            default -> log.debug("Unhandled webhook event: {}", event);
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
        // Проверка секрета отключена - используем Planka accessToken
        // Planka отправляет токен через Authorization Bearer header
        if (expectedWebhookSecret != null && !expectedWebhookSecret.isBlank() && webhookSecret != null) {
            if (!expectedWebhookSecret.equals(webhookSecret)) {
                log.debug("Webhook secret mismatch, but allowing request (expected: {}, got: {})", 
                        expectedWebhookSecret.substring(0, Math.min(8, expectedWebhookSecret.length())) + "...", 
                        webhookSecret.substring(0, Math.min(8, webhookSecret.length())) + "...");
            }
        }
        // Не выбрасываем исключение - разрешаем все webhook'и от Planka
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
        if (data == null) return;

        // Planka формат: cardId в data или data.item.id
        String cardId = data.getCardId();
        String listId = data.getListId();
        
        log.info("Card updated in Planka: cardId={}, listId={}", cardId, listId);
        
        if (cardId == null) {
            log.debug("Card update without cardId, skipping");
            return;
        }

        // Ищем RFC по plankaCardId
        Optional<RfcEntity> rfcOpt = rfcRepository.findByPlankaCardId(cardId);
        if (rfcOpt.isEmpty()) {
            log.debug("RFC not found for plankaCardId: {}, this card may not be linked to RFC", cardId);
            return;
        }

        RfcEntity rfc = rfcOpt.get();
        
        // Проверяем изменился ли список (статус)
        if (listId != null) {
            String listName = data.getListName();
            if (listName == null) {
                // Попробуем определить статус по listId через API
                listName = getListNameById(listId);
            }
            
            if (listName != null) {
                RfcStatus newStatus = LIST_NAME_TO_STATUS.get(listName.toLowerCase());
                RfcStatus oldStatus = rfc.getStatus();
                
                if (newStatus != null && newStatus != oldStatus) {
                    // Извлекаем информацию о пользователе из webhook (OIDC/SSO данные)
                    // Важно: Planka передаёт user на верхнем уровне payload, не внутри data!
                    var effectiveUser = payload.getEffectiveUser();
                    
                    // Если данные о пользователе не пришли - пробуем получить из Planka API
                    if (effectiveUser == null) {
                        log.info("User info not in webhook, fetching from Planka API...");
                        effectiveUser = fetchUserFromPlankaApi(cardId);
                    }
                    
                    String plankaUserId = effectiveUser != null ? effectiveUser.getId() : null;
                    String userName = effectiveUser != null ? effectiveUser.getName() : null;
                    String userUsername = effectiveUser != null ? effectiveUser.getUsername() : null;
                    String userEmail = effectiveUser != null ? effectiveUser.getEmail() : null;
                    
                    log.info("=== RFC STATUS CHANGE FROM PLANKA (cardUpdate/OIDC) ===");
                    log.info("RFC ID: {}", rfc.getId());
                    log.info("RFC Title: {}", rfc.getTitle());
                    log.info("Status Change: {} -> {}", oldStatus, newStatus);
                    log.info("Planka User ID: {}", plankaUserId);
                    log.info("Planka Username: {}", userUsername);
                    log.info("Planka Email: {}", userEmail);
                    log.info("Planka Name: {}", userName);
                    log.info("=======================================================");
                    
                    rfc.setStatus(newStatus);
                    // Устанавливаем метку времени чтобы scheduler не перезаписывал статус
                    rfc.setPlankaStatusChangedAt(OffsetDateTime.now());
                    rfcRepository.save(rfc);
                    
                    // Определяем пользователя через OIDC связку
                    UserEntity changedByUser = findUserFromPlankaWebhook(plankaUserId, userUsername, userName, userEmail);
                    
                    // Fallback на requester RFC если пользователь не найден в системе
                    if (changedByUser == null) {
                        log.warn("Using RFC requester as fallback for history (user not found via OIDC)");
                        changedByUser = rfc.getRequester();
                    }
                    
                    // Записываем в историю
                    createStatusChangeHistory(rfc, changedByUser, oldStatus, newStatus, plankaUserId, userUsername, userEmail);
                    
                    log.info("RFC {} status updated to {} by user: {} (OIDC resolved, protected for 5 minutes)", 
                            rfc.getId(), newStatus, changedByUser.getUsername());
                    return;
                }
            }
        }
        
        // Обновляем другие поля если нужно
        if (data.getName() != null && !data.getName().equals(rfc.getTitle())) {
            log.info("Updating RFC title from Planka: {} -> {}", rfc.getTitle(), data.getName());
            rfc.setTitle(data.getName());
            rfcRepository.save(rfc);
        }
        
        log.info("RFC card updated from Planka: rfcId={}", rfc.getId());
    }
    
    private String getListNameById(String listId) {
        // Получаем название списка по ID через API
        List<Map<String, Object>> lists = plankaClient.getBoardLists(defaultBoardId);
        return lists.stream()
                .filter(list -> listId.equals(list.get("id")))
                .map(list -> (String) list.get("name"))
                .findFirst()
                .orElse(null);
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
            // Извлекаем информацию о пользователе из webhook (OIDC/SSO данные)
            // Важно: Planka передаёт user на верхнем уровне payload, не внутри data!
            var effectiveUser = payload.getEffectiveUser();
            
            // Если данные о пользователе не пришли - пробуем получить из Planka API
            if (effectiveUser == null && cardId != null) {
                log.info("User info not in webhook, fetching from Planka API...");
                effectiveUser = fetchUserFromPlankaApi(cardId);
            }
            
            String plankaUserId = effectiveUser != null ? effectiveUser.getId() : null;
            String userName = effectiveUser != null ? effectiveUser.getName() : null;
            String userUsername = effectiveUser != null ? effectiveUser.getUsername() : null;
            String userEmail = effectiveUser != null ? effectiveUser.getEmail() : null;
            
            log.info("=== RFC STATUS CHANGE FROM PLANKA (OIDC) ===");
            log.info("RFC ID: {}", rfc.getId());
            log.info("RFC Title: {}", rfc.getTitle());
            log.info("Status Change: {} -> {}", oldStatus, newStatus);
            log.info("Planka User ID: {}", plankaUserId);
            log.info("Planka Username: {}", userUsername);
            log.info("Planka Email: {}", userEmail);
            log.info("Planka Name: {}", userName);
            log.info("Source: Planka card move via OIDC");
            log.info("=============================================");
            
            rfc.setStatus(newStatus);
            // Устанавливаем метку времени чтобы scheduler не перезаписывал статус
            rfc.setPlankaStatusChangedAt(OffsetDateTime.now());
            rfcRepository.save(rfc);
            
            // Определяем пользователя через OIDC связку
            UserEntity changedByUser = findUserFromPlankaWebhook(plankaUserId, userUsername, userName, userEmail);
            
            // Fallback на requester RFC если пользователь не найден в системе
            if (changedByUser == null) {
                log.warn("Using RFC requester as fallback for history (user not found via OIDC)");
                changedByUser = rfc.getRequester();
            }
            
            createStatusChangeHistory(rfc, changedByUser, oldStatus, newStatus, plankaUserId, userUsername, userEmail);
            
            log.info("RFC {} status successfully updated to {} by user: {} (OIDC resolved, protected for 5 minutes)", 
                    rfc.getId(), newStatus, changedByUser.getUsername());
        }
    }

    /**
     * Получает информацию о пользователе из Planka API по ID карточки
     * Используется когда webhook не содержит данных о пользователе
     */
    private PlankaWebhookPayload.MovedByUser fetchUserFromPlankaApi(String cardId) {
        if (cardId == null) return null;
        
        try {
            Optional<Map<String, Object>> userOpt = plankaClient.getLastCardAction(cardId);
            if (userOpt.isPresent()) {
                Map<String, Object> user = userOpt.get();
                log.info("Fetched user from Planka API: {}", user);
                return PlankaWebhookPayload.MovedByUser.builder()
                        .id(user.get("id") != null ? user.get("id").toString() : null)
                        .username(user.get("username") != null ? user.get("username").toString() : null)
                        .name(user.get("name") != null ? user.get("name").toString() : null)
                        .email(user.get("email") != null ? user.get("email").toString() : null)
                        .build();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch user from Planka API: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Находит пользователя по данным из Planka webhook (OIDC/SSO интеграция)
     * Приоритет поиска:
     * 1. По plankaUserId (прямая связь через SSO)
     * 2. По email (OIDC claim)
     * 3. По username (OIDC preferred_username)
     * 
     * @param plankaUserId ID пользователя в Planka
     * @param username username из Planka (может совпадать с OIDC preferred_username)
     * @param name полное имя пользователя
     * @param email email пользователя (OIDC claim)
     * @return найденный пользователь или системный пользователь
     */
    private UserEntity findUserFromPlankaWebhook(String plankaUserId, String username, String name, String email) {
        log.debug("Resolving user from Planka webhook: plankaUserId={}, username={}, email={}", 
                plankaUserId, username, email);
        
        // 1. Приоритетный поиск по plankaUserId (прямая связь SSO)
        if (plankaUserId != null && !plankaUserId.isBlank()) {
            Optional<UserEntity> user = userRepository.findByPlankaUserId(plankaUserId);
            if (user.isPresent()) {
                log.debug("User found by plankaUserId: {} -> {}", plankaUserId, user.get().getUsername());
                return user.get();
            }
        }
        
        // 2. Поиск по email (OIDC email claim - наиболее надежный идентификатор)
        if (email != null && !email.isBlank()) {
            Optional<UserEntity> user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                log.debug("User found by email: {} -> {}", email, user.get().getUsername());
                // Обновляем plankaUserId для будущих запросов
                if (plankaUserId != null && user.get().getPlankaUserId() == null) {
                    user.get().setPlankaUserId(plankaUserId);
                    userRepository.save(user.get());
                    log.info("Updated plankaUserId for user {}: {}", user.get().getId(), plankaUserId);
                }
                return user.get();
            }
        }
        
        // 3. Поиск по username (OIDC preferred_username)
        if (username != null && !username.isBlank()) {
            Optional<UserEntity> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                log.debug("User found by username: {} -> {}", username, user.get().getUsername());
                // Обновляем plankaUserId для будущих запросов
                if (plankaUserId != null && user.get().getPlankaUserId() == null) {
                    user.get().setPlankaUserId(plankaUserId);
                    userRepository.save(user.get());
                    log.info("Updated plankaUserId for user {}: {}", user.get().getId(), plankaUserId);
                }
                return user.get();
            }
        }
        
        // 4. Пользователь не найден - используем системного пользователя (requester RFC)
        log.warn("User not found by plankaUserId='{}', email='{}', username='{}'. " +
                 "Please ensure user SSO sync is configured correctly.", 
                 plankaUserId, email, username);
        
        return null; // Вернём null, чтобы использовать requester RFC как fallback
    }

    /**
     * Создает запись в истории RFC об изменении статуса через Planka (OIDC/SSO)
     * 
     * @param rfc RFC сущность
     * @param changedBy пользователь, который изменил статус (найден через OIDC)
     * @param oldStatus предыдущий статус
     * @param newStatus новый статус
     * @param plankaUserId ID пользователя в Planka (для аудита)
     * @param plankaUsername username из Planka
     * @param plankaEmail email из Planka
     */
    private void createStatusChangeHistory(RfcEntity rfc, UserEntity changedBy, 
                                           RfcStatus oldStatus, RfcStatus newStatus,
                                           String plankaUserId, String plankaUsername, String plankaEmail) {
        Set<Long> affectedSubsystemIds = rfc.getAffectedSubsystems().stream()
                .map(RfcAffectedSubsystemEntity::getId)
                .collect(Collectors.toSet());

        // Формируем описание с информацией об OIDC источнике
        StringBuilder description = new StringBuilder();
        description.append(String.format("Статус изменен с %s на %s через Planka", oldStatus, newStatus));
        description.append("\n--- OIDC/SSO Info ---");
        description.append("\nPlanka User ID: ").append(plankaUserId != null ? plankaUserId : "N/A");
        description.append("\nPlanka Username: ").append(plankaUsername != null ? plankaUsername : "N/A");
        description.append("\nPlanka Email: ").append(plankaEmail != null ? plankaEmail : "N/A");
        description.append("\nRFC System User: ").append(changedBy.getUsername());
        description.append(" (ID: ").append(changedBy.getId()).append(")");

        RfcHistoryEntity history = RfcHistoryEntity.builder()
                .rfcId(rfc.getId())
                .operation(HistoryOperationType.STATUS_CHANGE)
                .changedBy(changedBy)
                .title(rfc.getTitle())
                .description(description.toString())
                .implementationDate(rfc.getImplementationDate())
                .urgency(rfc.getUrgency())
                .status(newStatus)
                .requester(rfc.getRequester())
                .attachmentIds(Set.of())
                .affectedSubsystems(affectedSubsystemIds)
                .build();

        rfcHistoryRepository.save(history);
        log.info("History record created for RFC {} status change: {} -> {} by {} (plankaUserId: {}, email: {})", 
                rfc.getId(), oldStatus, newStatus, changedBy.getUsername(), plankaUserId, plankaEmail);
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

