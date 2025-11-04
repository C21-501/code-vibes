package ru.c21501.rfcservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Роли пользователей в системе
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    /**
     * Инициатор изменений - может создавать RFC
     */
    REQUESTER("Инициатор"),

    /**
     * Исполнитель - может выполнять RFC
     */
    EXECUTOR("Исполнитель"),

    /**
     * Менеджер CAB - может согласовывать RFC
     */
    CAB_MANAGER("Менеджер CAB"),

    /**
     * Администратор - полный доступ
     */
    ADMIN("Администратор");

    private final String displayName;
}