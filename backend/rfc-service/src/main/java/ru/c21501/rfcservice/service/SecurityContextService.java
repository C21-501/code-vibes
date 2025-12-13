package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.UserEntity;

/**
 * Сервис для работы с SecurityContext
 */
public interface SecurityContextService {

    /**
     * Получает ID текущего аутентифицированного пользователя
     *
     * @return ID пользователя
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    UserEntity getCurrentUser();

    /**
     * Получает username текущего аутентифицированного пользователя
     *
     * @return username пользователя
     * @throws IllegalStateException если пользователь не аутентифицирован
     */
    String getCurrentUserKeycloakId();
}