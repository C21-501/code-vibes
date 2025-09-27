package ru.c21501.rfcservice.model.enums;

/**
 * Роли пользователей в системе RFC
 */
public enum UserRole {
    REQUESTER,    // Инициатор запросов
    EXECUTOR,     // Исполнитель
    CAB_MANAGER,  // Менеджер CAB (Change Advisory Board)
    ADMIN         // Администратор системы
}
