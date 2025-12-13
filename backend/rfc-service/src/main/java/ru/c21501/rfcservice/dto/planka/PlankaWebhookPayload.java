package ru.c21501.rfcservice.dto.planka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Payload webhook от Planka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlankaWebhookPayload {

    /**
     * Тип события
     * Возможные значения: card_created, card_updated, card_moved, card_deleted, rfc_status_changed
     */
    private String event;

    /**
     * Время события
     */
    private OffsetDateTime timestamp;

    /**
     * Данные события
     */
    private WebhookData data;

    /**
     * Источник события
     */
    private String source;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebhookData {
        
        /**
         * Item данные (Planka формат)
         */
        private Map<String, Object> item;
        
        /**
         * ID карточки в Planka
         */
        private String cardId;
        
        /**
         * Получить cardId из item или напрямую
         */
        public String getCardId() {
            if (cardId != null) return cardId;
            if (item != null && item.get("id") != null) {
                return item.get("id").toString();
            }
            return null;
        }
        
        /**
         * Получить listId из item или напрямую
         */
        public String getListId() {
            if (listId != null) return listId;
            if (item != null && item.get("listId") != null) {
                return item.get("listId").toString();
            }
            return null;
        }
        
        /**
         * Получить name из item или напрямую
         */
        public String getName() {
            if (name != null) return name;
            if (item != null && item.get("name") != null) {
                return item.get("name").toString();
            }
            return null;
        }

        /**
         * Название карточки
         */
        private String name;

        /**
         * Описание
         */
        private String description;

        /**
         * ID списка (колонки) в Planka
         */
        private String listId;

        /**
         * Название списка (колонки)
         */
        private String listName;

        /**
         * ID доски в Planka
         */
        private String boardId;

        /**
         * Название доски
         */
        private String boardName;

        /**
         * ID проекта в Planka
         */
        private String projectId;

        /**
         * RFC данные (если карточка типа RFC)
         */
        @JsonProperty("rfcData")
        private RfcData rfcData;

        /**
         * Предыдущий ID списка (для card_moved)
         */
        private String previousListId;

        /**
         * Предыдущее название списка
         */
        private String previousListName;

        /**
         * ID пользователя, сделавшего изменение
         */
        private String userId;

        /**
         * Имя пользователя
         */
        private String userName;

        /**
         * Информация о пользователе, который переместил карточку
         */
        private MovedByUser movedBy;

        /**
         * Дата дедлайна
         */
        private OffsetDateTime dueDate;

        /**
         * Завершено ли
         */
        private Boolean isCompleted;

        /**
         * Тип карточки
         */
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RfcData {
        
        /**
         * ID RFC в code-vibes (внешний ID)
         */
        @JsonProperty("externalRfcId")
        private Long externalRfcId;

        /**
         * Уникальный ID RFC в Planka
         */
        @JsonProperty("rfcId")
        private String rfcId;

        /**
         * Статус RFC
         */
        private String status;

        /**
         * Предыдущий статус (для rfc_status_changed)
         */
        private String previousStatus;

        /**
         * Срочность (EMERGENCY, URGENT, PLANNED)
         */
        private String urgency;

        /**
         * Дата внедрения
         */
        private OffsetDateTime implementationDate;

        /**
         * Затронутые системы
         */
        private List<AffectedSystem> affectedSystems;

        /**
         * Комментарий к изменению статуса
         */
        private String comment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AffectedSystem {
        
        private Long systemId;
        private String systemName;
        private List<AffectedSubsystem> subsystems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AffectedSubsystem {
        
        private Long subsystemId;
        private String subsystemName;
        private Long executorId;
        private String executorName;
    }

    /**
     * Информация о пользователе, который переместил карточку в Planka
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovedByUser {
        
        /**
         * ID пользователя в Planka
         */
        private String id;
        
        /**
         * Имя пользователя (username)
         */
        private String username;
        
        /**
         * Полное имя пользователя
         */
        private String name;
        
        /**
         * Email пользователя
         */
        private String email;
    }
}

