package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.c21501.rfcservice.dto.planka.PlankaWebhookPayload;
import ru.c21501.rfcservice.service.PlankaIntegrationService;

/**
 * Контроллер для обработки webhooks от Planka
 */
@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PlankaIntegrationService plankaIntegrationService;

    /**
     * Обработка webhook от Planka при изменении карточки
     */
    @PostMapping("/planka")
    public ResponseEntity<Void> handlePlankaWebhook(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String webhookSecret,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Planka-User-Id", required = false) String plankaUserId,
            @RequestHeader(value = "X-Planka-Username", required = false) String plankaUsername,
            @RequestHeader(value = "X-Planka-User-Email", required = false) String plankaEmail,
            @RequestBody PlankaWebhookPayload payload
    ) {
        var data = payload.getData();
        
        // Получаем пользователя с верхнего уровня payload (где Planka передаёт его)
        var effectiveUser = payload.getEffectiveUser();
        
        log.info("Received webhook from Planka: event={}, cardId={}", 
                payload.getEvent(), data != null ? data.getCardId() : null);
        
        // Логирование заголовков
        log.info("Webhook headers - Authorization: {}, X-Planka-User-Id: {}, X-Planka-Username: {}, X-Planka-User-Email: {}", 
                authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : null,
                plankaUserId, plankaUsername, plankaEmail);
        
        // Детальное логирование пользователя с верхнего уровня (SSO user от Planka)
        log.info("Webhook top-level user from Planka SSO: {}", payload.getUser());
        
        // Детальное логирование всех данных для отладки SSO
        if (data != null) {
            log.info("Webhook raw data - userId: {}, userName: {}, movedBy: {}, user: {}", 
                    data.getUserId(), data.getUserName(), data.getMovedBy(), data.getUser());
            if (data.getItem() != null) {
                log.debug("Webhook item data: {}", data.getItem());
            }
        }
        
        // Детальное логирование пользователя для OIDC/SSO отладки
        if (effectiveUser != null) {
            log.info("Webhook effective user (SSO): id={}, username={}, email={}, name={}", 
                    effectiveUser.getId(), effectiveUser.getUsername(), 
                    effectiveUser.getEmail(), effectiveUser.getName());
        } else {
            log.warn("Webhook received without user info - OIDC tracking will use fallback");
        }
        
        // Извлекаем токен из Authorization header если X-Webhook-Secret не указан
        String effectiveSecret = webhookSecret;
        if (effectiveSecret == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            effectiveSecret = authHeader.substring(7);
        }
        
        try {
            plankaIntegrationService.handlePlankaWebhook(payload, effectiveSecret);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            log.warn("Invalid webhook secret from Planka");
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Error processing Planka webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Обработка webhook при перемещении карточки между списками (изменение статуса RFC)
     */
    @PostMapping("/planka/card-moved")
    public ResponseEntity<Void> handleCardMoved(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String webhookSecret,
            @RequestBody PlankaWebhookPayload payload
    ) {
        var data = payload.getData();
        var movedBy = data != null ? data.getMovedBy() : null;
        
        // Детальное логирование с информацией о пользователе
        log.info("=== RFC CARD MOVED IN PLANKA ===");
        log.info("Payload: {}", payload.getData().toString());
        log.info("Card ID: {}", data != null ? data.getCardId() : "unknown");
        log.info("Card Name: {}", data != null ? data.getName() : "unknown");
        log.info("From List: {} -> To List: {}", 
                data != null ? data.getPreviousListName() : "unknown",
                data != null ? data.getListName() : "unknown");
        
        if (movedBy != null) {
            log.info("Moved By User: {} ({}) [ID: {}, Email: {}]", 
                    movedBy.getName(), 
                    movedBy.getUsername(),
                    movedBy.getId(),
                    movedBy.getEmail());
        } else {
            log.warn("User info not provided in webhook");
        }
        log.info("================================");
        
        try {
            plankaIntegrationService.handleCardMovedWebhook(payload, webhookSecret);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            log.warn("Invalid webhook secret from Planka");
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Error processing card moved webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Обработка webhook при обновлении карточки
     */
    @PostMapping("/planka/card-updated")
    public ResponseEntity<Void> handleCardUpdated(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String webhookSecret,
            @RequestBody PlankaWebhookPayload payload
    ) {
        log.info("Received card updated webhook from Planka: cardId={}", 
                payload.getData() != null ? payload.getData().getCardId() : null);
        
        try {
            plankaIntegrationService.handleCardUpdatedWebhook(payload, webhookSecret);
            return ResponseEntity.ok().build();
        } catch (SecurityException e) {
            log.warn("Invalid webhook secret from Planka");
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            log.error("Error processing card updated webhook: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check для Planka интеграции
     */
    @GetMapping("/planka/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Planka webhook endpoint is healthy");
    }
}

