package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.repository.UserRepository;
import ru.c21501.rfcservice.service.SecurityContextService;

/**
 * Реализация сервиса для работы с SecurityContext
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityContextServiceImpl implements SecurityContextService {

    private final UserRepository userRepository;

    @Override
    public UserEntity getCurrentUser() {
        String keycloakId = getCurrentUserKeycloakId();

        // Ищем пользователя по keycloakId
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User with keycloakId '%s' not found", keycloakId)
                ));
    }

    @Override
    public String getCurrentUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        String keycloakId = authentication.getName();
        log.debug("Current authenticated user: {}", keycloakId);

        return keycloakId;
    }
}