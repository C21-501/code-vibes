package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

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
