package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с пользователями
 */
public interface UserService {
    
    /**
     * Создать нового пользователя
     */
    User createUser(User user);
    
    /**
     * Обновить пользователя
     */
    User updateUser(User user);
    
    /**
     * Найти пользователя по ID
     */
    Optional<User> findById(UUID id);
    
    /**
     * Найти пользователя по Keycloak ID
     */
    Optional<User> findByKeycloakId(String keycloakId);
    
    /**
     * Найти пользователя по имени пользователя
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Найти пользователя по email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Получить всех пользователей
     */
    List<User> findAll();
    
    /**
     * Проверить существование пользователя по ID
     */
    boolean existsById(UUID id);
    
    /**
     * Проверить существование пользователя по Keycloak ID
     */
    boolean existsByKeycloakId(String keycloakId);
    
    /**
     * Проверить существование пользователя по имени пользователя
     */
    boolean existsByUsername(String username);
    
    /**
     * Проверить существование пользователя по email
     */
    boolean existsByEmail(String email);
}
