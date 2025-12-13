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
     * Обычный пользователь - может создавать RFC
     */
    USER("Пользователь"),

    /**
     * Согласующий RFC - может согласовывать изменения
     */
    RFC_APPROVER("Согласующий"),

    /**
     * Менеджер CAB - может согласовывать RFC и управлять процессом
     */
    CAB_MANAGER("Менеджер CAB"),

    /**
     * Администратор - полный доступ
     */
    ADMIN("Администратор");

    private final String displayName;
}