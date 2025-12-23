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
import java.util.Optional;

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

    @Override
    @Transactional
    public void syncUsersFromKeycloak() {
        log.info("Starting synchronization of users from Keycloak");

        try {
            // Получаем всех пользователей из Keycloak
            List<KeycloakUserDto> keycloakUsers = keycloakClient.getAllUsers();
            log.info("Found {} users in Keycloak", keycloakUsers.size());

            int created = 0;
            int updated = 0;
            int skipped = 0;

            for (KeycloakUserDto keycloakUser : keycloakUsers) {
                try {
                    // Пропускаем пользователей без ID или username
                    if (keycloakUser.getId() == null || keycloakUser.getUsername() == null) {
                        log.warn("Skipping user without ID or username: {}", keycloakUser);
                        skipped++;
                        continue;
                    }

                    // Проверяем, существует ли пользователь в БД
                    Optional<UserEntity> existingUser = userRepository.findByKeycloakId(keycloakUser.getId());

                    if (existingUser.isPresent()) {
                        // Обновляем существующего пользователя
                        UserEntity user = existingUser.get();
                        boolean needsUpdate = false;

                        // Обновляем firstName, если изменился
                        if (keycloakUser.getFirstName() != null && !keycloakUser.getFirstName().equals(user.getFirstName())) {
                            user.setFirstName(keycloakUser.getFirstName());
                            needsUpdate = true;
                        }

                        // Обновляем lastName, если изменился
                        if (keycloakUser.getLastName() != null && !keycloakUser.getLastName().equals(user.getLastName())) {
                            user.setLastName(keycloakUser.getLastName());
                            needsUpdate = true;
                        }

                        // Синхронизируем роль пользователя из Keycloak
                        ru.c21501.rfcservice.model.enums.UserRole syncedRole = syncUserRoleFromKeycloak(keycloakUser.getId());
                        if (syncedRole != null && !syncedRole.equals(user.getRole())) {
                            user.setRole(syncedRole);
                            needsUpdate = true;
                        }

                        if (needsUpdate) {
                            userRepository.save(user);
                            log.debug("Updated user: {} ({})", user.getUsername(), user.getKeycloakId());
                            updated++;
                        } else {
                            log.debug("No changes for user: {} ({})", user.getUsername(), user.getKeycloakId());
                            skipped++;
                        }
                    } else {
                        // Создаем нового пользователя
                        // Проверяем, что есть необходимые данные
                        if (keycloakUser.getFirstName() == null || keycloakUser.getLastName() == null) {
                            log.warn("Skipping user without firstName or lastName: {}", keycloakUser.getUsername());
                            skipped++;
                            continue;
                        }

                        // Получаем роль пользователя из Keycloak
                        ru.c21501.rfcservice.model.enums.UserRole role = syncUserRoleFromKeycloak(keycloakUser.getId());
                        if (role == null) {
                            // Если роль не найдена, используем USER по умолчанию
                            role = ru.c21501.rfcservice.model.enums.UserRole.USER;
                            log.debug("No role found for user {}, using default role: USER", keycloakUser.getUsername());
                        }

                        UserEntity newUser = UserEntity.builder()
                                .username(keycloakUser.getUsername())
                                .firstName(keycloakUser.getFirstName())
                                .lastName(keycloakUser.getLastName())
                                .keycloakId(keycloakUser.getId())
                                .role(role)
                                .build();

                        userRepository.save(newUser);
                        log.debug("Created new user: {} ({})", newUser.getUsername(), newUser.getKeycloakId());
                        created++;
                    }
                } catch (Exception e) {
                    log.error("Error syncing user {}: {}", keycloakUser.getUsername(), e.getMessage(), e);
                    skipped++;
                }
            }

            log.info("User synchronization completed. Created: {}, Updated: {}, Skipped: {}", created, updated, skipped);
        } catch (Exception e) {
            log.error("Error during user synchronization from Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to synchronize users from Keycloak", e);
        }
    }

    /**
     * Синхронизирует роль пользователя из Keycloak
     * Получает роли пользователя из Keycloak и возвращает первую подходящую роль из системы
     *
     * @param keycloakUserId ID пользователя в Keycloak
     * @return роль пользователя или null, если роль не найдена
     */
    private ru.c21501.rfcservice.model.enums.UserRole syncUserRoleFromKeycloak(String keycloakUserId) {
        try {
            List<KeycloakRoleDto> keycloakRoles = keycloakClient.getUserRealmRoles(keycloakUserId);

            // Ищем первую подходящую роль из системы в порядке приоритета
            for (ru.c21501.rfcservice.model.enums.UserRole systemRole : ru.c21501.rfcservice.model.enums.UserRole.values()) {
                for (KeycloakRoleDto keycloakRole : keycloakRoles) {
                    if (systemRole.name().equalsIgnoreCase(keycloakRole.getName())) {
                        return systemRole;
                    }
                }
            }

            log.debug("No matching system role found for user {} with Keycloak roles: {}",
                    keycloakUserId, keycloakRoles.stream().map(KeycloakRoleDto::getName).toList());
            return null;
        } catch (Exception e) {
            log.error("Error getting roles for user {}: {}", keycloakUserId, e.getMessage());
            return null;
        }
    }
}