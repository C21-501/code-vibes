package ru.c21501.rfcservice.model.enums;

/**
 * Статус подтверждения исполнения RFC командой
 */
public enum ConfirmationStatus {
    PENDING,    // Ожидает подтверждения
    CONFIRMED,  // Подтверждено
    IMPOSSIBLE  // Невозможно выполнить
}
