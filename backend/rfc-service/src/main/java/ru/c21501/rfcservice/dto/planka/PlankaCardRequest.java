package ru.c21501.rfcservice.dto.planka;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Запрос на создание/обновление карточки в Planka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlankaCardRequest {

    /**
     * Название карточки
     */
    private String name;

    /**
     * Описание
     */
    private String description;

    /**
     * ID списка, в который помещается карточка
     */
    private String listId;

    /**
     * Позиция карточки в списке
     */
    private Double position;

    /**
     * Тип карточки
     */
    private String type;

    /**
     * Дата дедлайна
     */
    private OffsetDateTime dueDate;

    /**
     * RFC специфичные данные
     */
    @JsonProperty("rfcData")
    private RfcCardData rfcData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RfcCardData {
        
        /**
         * ID RFC в code-vibes
         */
        @JsonProperty("externalRfcId")
        private Long externalRfcId;

        /**
         * Статус RFC
         */
        private String status;

        /**
         * Срочность
         */
        private String urgency;

        /**
         * Дата внедрения
         */
        private OffsetDateTime implementationDate;

        /**
         * Затронутые системы (сериализованные)
         */
        private List<Map<String, Object>> affectedSystems;

        /**
         * ID создателя
         */
        private Long requesterId;

        /**
         * Имя создателя
         */
        private String requesterName;
    }
}

