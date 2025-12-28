package ru.c21501.rfcservice.dto.planka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Ответ от Planka API при работе с карточками
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlankaCardResponse {

    /**
     * ID карточки
     */
    private String id;

    /**
     * Название
     */
    private String name;

    /**
     * Описание
     */
    private String description;

    /**
     * ID списка
     */
    private String listId;

    /**
     * ID доски
     */
    private String boardId;

    /**
     * ID проекта
     */
    private String projectId;

    /**
     * Позиция
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
     * RFC данные
     */
    @JsonProperty("rfcData")
    private Map<String, Object> rfcData;

    /**
     * Дата создания
     */
    private OffsetDateTime createdAt;

    /**
     * Дата обновления
     */
    private OffsetDateTime updatedAt;
}

