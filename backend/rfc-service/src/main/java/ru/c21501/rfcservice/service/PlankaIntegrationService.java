package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.dto.planka.PlankaWebhookPayload;
import ru.c21501.rfcservice.model.entity.RfcEntity;

/**
 * Сервис интеграции с Planka
 */
public interface PlankaIntegrationService {

    /**
     * Обработка webhook от Planka
     */
    void handlePlankaWebhook(PlankaWebhookPayload payload, String webhookSecret);

    /**
     * Обработка webhook при перемещении карточки
     */
    void handleCardMovedWebhook(PlankaWebhookPayload payload, String webhookSecret);

    /**
     * Обработка webhook при обновлении карточки
     */
    void handleCardUpdatedWebhook(PlankaWebhookPayload payload, String webhookSecret);

    /**
     * Синхронизировать RFC с Planka (создать или обновить карточку)
     */
    void syncRfcToPlanka(RfcEntity rfc);

    /**
     * Создать карточку в Planka для RFC
     */
    String createPlankaCardForRfc(RfcEntity rfc);

    /**
     * Обновить карточку в Planka для RFC
     */
    void updatePlankaCardForRfc(RfcEntity rfc, String plankaCardId);

    /**
     * Обновить статус RFC в Planka (переместить карточку в нужный список)
     */
    void updateRfcStatusInPlanka(RfcEntity rfc, String plankaCardId);

    /**
     * Удалить карточку из Planka
     */
    void deletePlankaCard(String plankaCardId);
}

