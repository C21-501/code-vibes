package ru.c21501.rfcservice.model.enums;

/**
 * Статусы RFC (Request for Change)
 */
public enum RfcStatus {
    REQUESTED_NEW,    // Новый запрос
    WAITING,          // Ожидает рассмотрения
    APPROVED,         // Одобрен
    WAITING_FOR_CAB,  // Ожидает рассмотрения CAB
    DECLINED,         // Отклонен
    DONE,             // Выполнен
    CANCELLED         // Отменен
}
