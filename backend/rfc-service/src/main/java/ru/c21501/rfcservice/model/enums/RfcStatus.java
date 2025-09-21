package ru.c21501.rfcservice.model.enums;

/**
 * Статусы RFC (Request for Change)
 */
public enum RfcStatus {
    DRAFT,           // Черновик
    SUBMITTED,       // Подано на рассмотрение
    UNDER_REVIEW,    // На рассмотрении
    APPROVED,        // Одобрено
    REJECTED,        // Отклонено
    IMPLEMENTED,     // Реализовано
    CANCELLED        // Отменено
}
