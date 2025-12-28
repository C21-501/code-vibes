package ru.c21501.rfcservice.model.enums;

/**
 * Тип операции в истории изменений
 */
public enum HistoryOperationType {
    /**
     * Создание
     */
    CREATE,

    /**
     * Обновление
     */
    UPDATE,

    /**
     * Удаление
     */
    DELETE,

    /**
     * Изменение статуса (перемещение карточки в Planka)
     */
    STATUS_CHANGE
}