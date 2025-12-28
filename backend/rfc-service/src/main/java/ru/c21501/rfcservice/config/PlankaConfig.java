package ru.c21501.rfcservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация интеграции с Planka
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "planka")
public class PlankaConfig {

    /**
     * Включена ли интеграция с Planka
     */
    private boolean enabled = false;

    /**
     * URL Planka API
     */
    private String url = "http://localhost:3000";

    /**
     * API токен для аутентификации в Planka
     */
    private String apiToken;

    /**
     * Секрет для валидации webhook
     */
    private String webhookSecret;

    /**
     * ID проекта RFC в Planka
     */
    private String projectId;

    /**
     * ID доски RFC в Planka
     */
    private String boardId;

    /**
     * Автоматически создавать карточки в Planka при создании RFC
     */
    private boolean autoSync = true;

    /**
     * Автоматически синхронизировать пользователей с Planka при создании
     */
    private boolean userSync = true;

    /**
     * Список названий для статуса NEW
     */
    private String listNameNew = "Новые";

    /**
     * Список названий для статуса UNDER_REVIEW
     */
    private String listNameUnderReview = "На рассмотрении";

    /**
     * Список названий для статуса APPROVED
     */
    private String listNameApproved = "Утверждено";

    /**
     * Список названий для статуса IMPLEMENTED
     */
    private String listNameImplemented = "Выполнено";

    /**
     * Список названий для статуса REJECTED
     */
    private String listNameRejected = "Отклонено";
}

