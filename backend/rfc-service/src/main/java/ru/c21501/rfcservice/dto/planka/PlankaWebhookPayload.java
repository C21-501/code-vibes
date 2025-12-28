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
     * Предыдущие данные (для сравнения изменений)
     */
    private WebhookData prevData;
    
    /**
     * Пользователь, выполнивший действие (из Planka OIDC/SSO)
     * Это поле приходит на ВЕРХНЕМ уровне webhook от Planka!
     */
    private MovedByUser user;

    /**
     * Источник события
     */
    private String source;
    
    /**
     * Получить пользователя из webhook (приоритет: user -> data.user)
     */
    public MovedByUser getEffectiveUser() {
        // 1. Верхний уровень - главный источник от Planka
        if (user != null) {
            return user;
        }
        // 2. Fallback на data если есть
        if (data != null) {
            return data.getEffectiveUser();
        }
        return null;
    }

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
         * Информация о пользователе из Planka (альтернативное поле)
         */
        private MovedByUser user;

        /**
         * Получить информацию о пользователе (приоритет: movedBy -> user -> item.user)
         */
        @SuppressWarnings("unchecked")
        public MovedByUser getEffectiveUser() {
            // 1. Приоритет: movedBy
            if (movedBy != null) {
                return movedBy;
            }
            
            // 2. Альтернатива: user
            if (user != null) {
                return user;
            }
            
            // 3. Извлекаем из item.user если есть
            if (item != null && item.get("user") != null) {
                Object userObj = item.get("user");
                if (userObj instanceof Map) {
                    Map<String, Object> userMap = (Map<String, Object>) userObj;
                    return MovedByUser.builder()
                            .id(userMap.get("id") != null ? userMap.get("id").toString() : null)
                            .username(userMap.get("username") != null ? userMap.get("username").toString() : null)
                            .name(userMap.get("name") != null ? userMap.get("name").toString() : null)
                            .email(userMap.get("email") != null ? userMap.get("email").toString() : null)
                            .build();
                }
            }
            
            // 4. Fallback: создаём из userId/userName если есть
            if (userId != null || userName != null) {
                return MovedByUser.builder()
                        .id(userId)
                        .name(userName)
                        .build();
            }
            
            return null;
        }

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

