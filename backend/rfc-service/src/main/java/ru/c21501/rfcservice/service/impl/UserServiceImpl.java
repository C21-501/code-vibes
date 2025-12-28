package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.client.KeycloakClient;
import ru.c21501.rfcservice.client.PlankaClient;
import ru.c21501.rfcservice.client.dto.KeycloakCredentialDto;
import ru.c21501.rfcservice.client.dto.KeycloakRoleDto;
import ru.c21501.rfcservice.client.dto.KeycloakUserDto;
import ru.c21501.rfcservice.config.PlankaConfig;
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
    private final PlankaClient plankaClient;
    private final PlankaConfig plankaConfig;

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

        // Генерируем email если не указан
        String email = userEntity.getEmail();
        if (email == null || email.isBlank()) {
            email = userEntity.getUsername() + "@local.domain";
            userEntity.setEmail(email);
        }

        // Создаем пользователя в Keycloak с паролем
        KeycloakUserDto keycloakUser = KeycloakUserDto.builder()
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(email)
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

        // Синхронизируем с Planka если интеграция включена
        syncUserToPlanka(userEntity, password);

        // Сохраняем пользователя в БД
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User saved to database with ID: {}, keycloakId: {}, plankaUserId: {}", 
                savedUser.getId(), savedUser.getKeycloakId(), savedUser.getPlankaUserId());

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

        // Обновляем email если указан
        if (userEntity.getEmail() != null && !userEntity.getEmail().isBlank()) {
            existingUser.setEmail(userEntity.getEmail());
        }

        // Обновляем пользователя в Keycloak, если есть keycloakId
        if (existingUser.getKeycloakId() != null) {
            String email = existingUser.getEmail() != null ? existingUser.getEmail() : existingUser.getUsername() + "@local.domain";
            
            // НЕ отправляем username в Keycloak (он read-only)
            KeycloakUserDto keycloakUser = KeycloakUserDto.builder()
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(email)
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

        // Синхронизируем с Planka если интеграция включена
        updateUserInPlanka(existingUser, oldRole);

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

    // ==================== PLANKA USER SYNC ====================

    /**
     * Синхронизирует пользователя с Planka при создании
     * 
     * @param userEntity пользователь для синхронизации
     * @param password пароль пользователя (опционально для SSO)
     */
    private void syncUserToPlanka(UserEntity userEntity, String password) {
        if (!plankaConfig.isEnabled() || !plankaConfig.isUserSync()) {
            log.debug("Planka user sync is disabled, skipping");
            return;
        }

        log.info("Syncing user to Planka: username={}, email={}", userEntity.getUsername(), userEntity.getEmail());

        try {
            String plankaRole = PlankaClient.mapRfcRoleToPlankaRole(userEntity.getRole().name());
            String fullName = userEntity.getFullName();
            String email = userEntity.getEmail();
            String username = userEntity.getUsername();

            // Сначала проверяем, существует ли уже пользователь с таким email
            Optional<String> existingUserId = plankaClient.findUserByEmail(email);
            
            if (existingUserId.isPresent()) {
                // Пользователь уже существует - связываем
                log.info("User already exists in Planka with ID: {}, linking", existingUserId.get());
                userEntity.setPlankaUserId(existingUserId.get());
                
                // Обновляем данные в Planka
                plankaClient.updateUser(existingUserId.get(), fullName, username, plankaRole);
            } else {
                // Создаем нового пользователя
                // Для SSO пользователей пароль может быть null - Planka создаст SSO пользователя
                Optional<String> plankaUserId = plankaClient.createUser(
                        email, 
                        fullName, 
                        username, 
                        password,  // Может быть null для SSO
                        plankaRole
                );

                if (plankaUserId.isPresent()) {
                    userEntity.setPlankaUserId(plankaUserId.get());
                    log.info("User created in Planka with ID: {}", plankaUserId.get());
                } else {
                    log.warn("Failed to create user in Planka, continuing without plankaUserId");
                }
            }
        } catch (Exception e) {
            // Не прерываем создание пользователя при ошибке Planka
            log.error("Error syncing user to Planka: {}", e.getMessage(), e);
        }
    }

    /**
     * Обновляет пользователя в Planka
     * 
     * @param userEntity пользователь для обновления
     * @param oldRole старая роль (для определения изменения роли)
     */
    private void updateUserInPlanka(UserEntity userEntity, ru.c21501.rfcservice.model.enums.UserRole oldRole) {
        if (!plankaConfig.isEnabled() || !plankaConfig.isUserSync()) {
            log.debug("Planka user sync is disabled, skipping update");
            return;
        }

        if (userEntity.getPlankaUserId() == null) {
            log.debug("User {} does not have plankaUserId, skipping Planka update", userEntity.getId());
            return;
        }

        log.info("Updating user in Planka: plankaUserId={}", userEntity.getPlankaUserId());

        try {
            String plankaRole = PlankaClient.mapRfcRoleToPlankaRole(userEntity.getRole().name());
            String fullName = userEntity.getFullName();

            boolean success = plankaClient.updateUser(
                    userEntity.getPlankaUserId(),
                    fullName,
                    null, // username не меняем
                    plankaRole
            );

            if (success) {
                log.info("User updated successfully in Planka");
            } else {
                log.warn("Failed to update user in Planka");
            }
        } catch (Exception e) {
            // Не прерываем обновление при ошибке Planka
            log.error("Error updating user in Planka: {}", e.getMessage(), e);
        }
    }
}