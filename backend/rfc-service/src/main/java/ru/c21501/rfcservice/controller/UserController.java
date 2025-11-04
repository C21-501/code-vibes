package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.client.KeycloakClient;
import ru.c21501.rfcservice.client.dto.KeycloakTokenResponse;
import ru.c21501.rfcservice.openapi.api.UsersApi;
import ru.c21501.rfcservice.openapi.model.LoginRequest;
import ru.c21501.rfcservice.openapi.model.LoginResponse;
import ru.c21501.rfcservice.openapi.model.UpdateUserRequest;
import ru.c21501.rfcservice.openapi.model.UserPageResponse;
import ru.c21501.rfcservice.openapi.model.UserRequest;
import ru.c21501.rfcservice.openapi.model.UserResponse;
import ru.c21501.rfcservice.service.UserApiService;

/**
 * Контроллер для работы с пользователями
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserApiService userApiService;
    private final KeycloakClient keycloakClient;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(UserRequest userRequest) {
        log.info("POST /api/users - Creating user: {}", userRequest.getUsername());
        return userApiService.createUser(userRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        userApiService.deleteUser(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUserById(Long id) {
        log.info("GET /api/users/{} - Getting user by ID", id);
        return userApiService.getUserById(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserPageResponse getUsers(Integer page, Integer size, String searchString) {
        log.info("GET /api/users - Getting users list (page: {}, size: {}, searchString: {})", page, size, searchString);
        return userApiService.getUsers(page, size, searchString);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        log.info("PUT /api/users/{} - Updating user: {}", id, updateUserRequest.getUsername());
        return userApiService.updateUser(id, updateUserRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(LoginRequest loginRequest) {
        log.info("POST /api/users/login - Login request for user: {}", loginRequest.getUsername());

        // Аутентификация через Keycloak
        KeycloakTokenResponse keycloakResponse = keycloakClient.login(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        // Маппинг Keycloak ответа в наш контракт
        LoginResponse response = new LoginResponse();
        response.setAccessToken(keycloakResponse.getAccessToken());
        response.setRefreshToken(keycloakResponse.getRefreshToken());
        response.setExpiresIn(keycloakResponse.getExpiresIn() != null
                ? keycloakResponse.getExpiresIn().intValue()
                : null);
        response.setRefreshExpiresIn(keycloakResponse.getRefreshExpiresIn() != null
                ? keycloakResponse.getRefreshExpiresIn().intValue()
                : null);
        response.setTokenType(keycloakResponse.getTokenType());

        log.info("User {} successfully authenticated", loginRequest.getUsername());
        return response;
    }
}