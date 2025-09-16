package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.repository.UserRepository;
import ru.c21501.rfcservice.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация сервиса для работы с пользователями
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public User createUser(User user) {
        log.debug("Создание пользователя: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        log.info("Создан пользователь с ID: {}", savedUser.getId());
        return savedUser;
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        log.debug("Обновление пользователя с ID: {}", user.getId());
        User updatedUser = userRepository.save(user);
        log.info("Обновлен пользователь с ID: {}", updatedUser.getId());
        return updatedUser;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        log.debug("Поиск пользователя по ID: {}", id);
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByKeycloakId(String keycloakId) {
        log.debug("Поиск пользователя по Keycloak ID: {}", keycloakId);
        return userRepository.findByKeycloakId(keycloakId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Поиск пользователя по имени пользователя: {}", username);
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.debug("Поиск пользователя по email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.debug("Получение всех пользователей");
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        log.debug("Проверка существования пользователя по ID: {}", id);
        return userRepository.existsById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByKeycloakId(String keycloakId) {
        log.debug("Проверка существования пользователя по Keycloak ID: {}", keycloakId);
        return userRepository.existsByKeycloakId(keycloakId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        log.debug("Проверка существования пользователя по имени пользователя: {}", username);
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("Проверка существования пользователя по email: {}", email);
        return userRepository.existsByEmail(email);
    }
}
