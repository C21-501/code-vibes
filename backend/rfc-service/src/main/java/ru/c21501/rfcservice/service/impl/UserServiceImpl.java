package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.client.KeycloakClient;
import ru.c21501.rfcservice.client.dto.KeycloakCredentialDto;
import ru.c21501.rfcservice.client.dto.KeycloakRoleDto;
import ru.c21501.rfcservice.client.dto.KeycloakUserDto;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.exception.UserAlreadyExistsException;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.repository.UserRepository;
import ru.c21501.rfcservice.service.UserService;

import java.util.List;

/**
 * Реализация сервиса для работы с пользователями
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakClient keycloakClient;

    @Override
    @Transactional
    public UserEntity createUser(UserEntity userEntity, String password) {
        log.info("Creating user: {}", userEntity.getUsername());

        // Проверяем, что пользователь с таким username еще не существует
        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new UserAlreadyExistsException(
                    String.format("User with username '%s' already exists", userEntity.getUsername())
            );
        }

        // Создаем пользователя в Keycloak с паролем
        KeycloakUserDto keycloakUser = KeycloakUserDto.builder()
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getUsername() + "@local.domain") // email обязателен для работы аккаунта
                .enabled(true)
                .emailVerified(true) // проставляем verified=true
                .credentials(List.of(KeycloakCredentialDto.password(password, false)))
                .build();

        String keycloakUserId = keycloakClient.createUser(keycloakUser);
        log.info("User created in Keycloak with ID: {}", keycloakUserId);

        // Назначаем роль пользователю в Keycloak
        assignRoleToUser(keycloakUserId, userEntity.getRole());

        // Сохраняем keycloakId в сущность
        userEntity.setKeycloakId(keycloakUserId);

        // Сохраняем пользователя в БД
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User saved to database with ID: {}, keycloakId: {}", savedUser.getId(), savedUser.getKeycloakId());

        return savedUser;
    }

    @Override
    @Transactional
    public UserEntity updateUser(Long id, UserEntity userEntity) {
        log.info("Updating user with ID: {}", id);

        // Проверяем, что пользователь существует
        UserEntity existingUser = getUserById(id);

        // Username нельзя изменить (read-only в Keycloak)
        if (!existingUser.getUsername().equals(userEntity.getUsername())) {
            throw new IllegalArgumentException("Username cannot be changed");
        }

        // Сохраняем старую роль для обновления в Keycloak
        ru.c21501.rfcservice.model.enums.UserRole oldRole = existingUser.getRole();

        // Обновляем данные (без username)
        existingUser.setFirstName(userEntity.getFirstName());
        existingUser.setLastName(userEntity.getLastName());
        existingUser.setRole(userEntity.getRole());

        // Обновляем пользователя в Keycloak, если есть keycloakId
        if (existingUser.getKeycloakId() != null) {
            // НЕ отправляем username в Keycloak (он read-only)
            KeycloakUserDto keycloakUser = KeycloakUserDto.builder()
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(existingUser.getUsername() + "@local.domain") // email обязателен
                    .enabled(true)
                    .emailVerified(true) // проставляем verified=true
                    .build();

            keycloakClient.updateUser(existingUser.getKeycloakId(), keycloakUser);
            log.info("User updated in Keycloak: {}", existingUser.getKeycloakId());

            // Обновляем роль, если она изменилась
            updateUserRole(existingUser.getKeycloakId(), userEntity.getRole(), oldRole);
        } else {
            log.warn("User {} does not have keycloakId, skipping Keycloak update", id);
        }

        // Сохраняем обновленного пользователя
        UserEntity updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", updatedUser.getId());

        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserById(Long id) {
        log.info("Getting user by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with ID %d not found", id)
                ));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        // Проверяем, что пользователь существует
        UserEntity user = getUserById(id);

        // Удаляем пользователя из Keycloak, если есть keycloakId
        if (user.getKeycloakId() != null) {
            keycloakClient.deleteUser(user.getKeycloakId());
            log.info("User deleted from Keycloak: {}", user.getKeycloakId());
        } else {
            log.warn("User {} does not have keycloakId, skipping Keycloak deletion", id);
        }

        // Удаляем пользователя из БД
        userRepository.delete(user);
        log.info("User deleted from database: {}", id);
    }

    /**
     * Назначает роль пользователю в Keycloak
     */
    private void assignRoleToUser(String keycloakUserId, ru.c21501.rfcservice.model.enums.UserRole userRole) {
        log.info("Assigning role {} to user {}", userRole, keycloakUserId);

        // Получаем роль из Keycloak по имени
        KeycloakRoleDto role = keycloakClient.getRealmRole(userRole.name());

        if (role != null) {
            // Назначаем роль
            keycloakClient.assignRealmRolesToUser(keycloakUserId, List.of(role));
            log.info("Role {} assigned successfully", userRole);
        } else {
            log.warn("Role {} not found in Keycloak, skipping role assignment", userRole);
        }
    }

    /**
     * Обновляет роль пользователя в Keycloak
     */
    private void updateUserRole(String keycloakUserId,
                                ru.c21501.rfcservice.model.enums.UserRole newRole,
                                ru.c21501.rfcservice.model.enums.UserRole oldRole) {
        if (newRole.equals(oldRole)) {
            log.info("Role hasn't changed, skipping role update");
            return;
        }

        log.info("Updating user role from {} to {}", oldRole, newRole);

        // Удаляем старую роль
        KeycloakRoleDto oldKeycloakRole = keycloakClient.getRealmRole(oldRole.name());
        if (oldKeycloakRole != null) {
            keycloakClient.removeRealmRolesFromUser(keycloakUserId, List.of(oldKeycloakRole));
            log.info("Old role {} removed", oldRole);
        }

        // Назначаем новую роль
        KeycloakRoleDto newKeycloakRole = keycloakClient.getRealmRole(newRole.name());
        if (newKeycloakRole != null) {
            keycloakClient.assignRealmRolesToUser(keycloakUserId, List.of(newKeycloakRole));
            log.info("New role {} assigned", newRole);
        } else {
            log.warn("New role {} not found in Keycloak", newRole);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserEntity> getUsers(String searchString, Pageable pageable) {
        log.info("Getting users with searchString: {}, page: {}, size: {}",
                searchString, pageable.getPageNumber(), pageable.getPageSize());

        Page<UserEntity> users;

        if (searchString != null && !searchString.trim().isEmpty()) {
            // Поиск по строке
            users = userRepository.searchUsers(searchString.trim(), pageable);
            log.info("Found {} users matching search string '{}'", users.getTotalElements(), searchString);
        } else {
            // Получить всех пользователей
            users = userRepository.findAll(pageable);
            log.info("Retrieved all users, total: {}", users.getTotalElements());
        }

        return users;
    }
}