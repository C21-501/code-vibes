package ru.c21501.rfcservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ru.c21501.rfcservice.client.dto.KeycloakRoleDto;
import ru.c21501.rfcservice.client.dto.KeycloakTokenResponse;
import ru.c21501.rfcservice.client.dto.KeycloakUserDto;
import ru.c21501.rfcservice.exception.KeycloakApiException;

import java.util.List;

/**
 * Клиент для взаимодействия с Keycloak Admin API
 */
@Slf4j
@Component
@SuppressWarnings("unused")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class KeycloakClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${keycloak.auth-server-url}")
    private String baseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    // Admin credentials for Keycloak Admin API access
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    /**
     * Создает пользователя в Keycloak
     *
     * @param userDto данные пользователя (включая credentials для пароля)
     * @return ID созданного пользователя в Keycloak
     */
    public String createUser(KeycloakUserDto userDto) {
        log.info("Creating user in Keycloak: {}", userDto.getUsername());

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/users", baseUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<KeycloakUserDto> request = new HttpEntity<>(userDto, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            // Keycloak returns the user ID in the Location header
            String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
            if (location != null) {
                String userId = location.substring(location.lastIndexOf('/') + 1);
                log.info("User created successfully in Keycloak with ID: {}", userId);
                return userId;
            }

            throw new KeycloakApiException("Failed to extract user ID from response", 500, null);

        } catch (HttpClientErrorException e) {
            log.error("Client error creating user in Keycloak (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());

            String errorMessage = parseErrorMessage(e.getResponseBodyAsString(),
                    "Не удалось создать пользователя в Keycloak");

            // Проверяем на конфликт (409) - пользователь уже существует
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new KeycloakApiException(
                        "Пользователь с таким именем уже существует в Keycloak",
                        e.getStatusCode().value(),
                        e.getResponseBodyAsString()
                );
            }

            throw new KeycloakApiException(
                    errorMessage,
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (HttpServerErrorException e) {
            log.error("Server error creating user in Keycloak (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakApiException(
                    "Ошибка сервера Keycloak при создании пользователя",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (Exception e) {
            log.error("Unexpected error creating user in Keycloak: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to create user in Keycloak", 500, null, e);
        }
    }

    /**
     * Обновляет пользователя в Keycloak
     *
     * @param keycloakUserId ID пользователя в Keycloak
     * @param userDto        данные пользователя
     */
    public void updateUser(String keycloakUserId, KeycloakUserDto userDto) {
        log.info("Updating user in Keycloak: {} (ID: {})", userDto.getUsername(), keycloakUserId);

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/users/%s", baseUrl, realm, keycloakUserId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<KeycloakUserDto> request = new HttpEntity<>(userDto, headers);

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    Void.class
            );
            log.info("User updated successfully in Keycloak: {}", keycloakUserId);

        } catch (HttpClientErrorException e) {
            log.error("Client error updating user in Keycloak (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());

            String errorMessage = parseErrorMessage(e.getResponseBodyAsString(),
                    "Не удалось обновить пользователя в Keycloak");

            // Проверяем на конфликт (409) - username уже занят
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new KeycloakApiException(
                        "Пользователь с таким именем уже существует в Keycloak",
                        e.getStatusCode().value(),
                        e.getResponseBodyAsString()
                );
            }

            // Проверяем на 404 - пользователь не найден
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new KeycloakApiException(
                        "Пользователь не найден в Keycloak",
                        e.getStatusCode().value(),
                        e.getResponseBodyAsString()
                );
            }

            throw new KeycloakApiException(
                    errorMessage,
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (HttpServerErrorException e) {
            log.error("Server error updating user in Keycloak (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakApiException(
                    "Ошибка сервера Keycloak при обновлении пользователя",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (Exception e) {
            log.error("Unexpected error updating user in Keycloak: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to update user in Keycloak", 500, null, e);
        }
    }

    /**
     * Удаляет пользователя из Keycloak
     *
     * @param keycloakUserId ID пользователя в Keycloak
     */
    public void deleteUser(String keycloakUserId) {
        log.info("Deleting user from Keycloak: {}", keycloakUserId);

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/users/%s", baseUrl, realm, keycloakUserId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
            log.info("User deleted successfully from Keycloak: {}", keycloakUserId);

        } catch (HttpClientErrorException e) {
            log.error("Client error deleting user from Keycloak (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());

            // Проверяем на 404 - пользователь не найден
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("User not found in Keycloak, considering as deleted: {}", keycloakUserId);
                return; // Считаем успешным, если пользователь уже не существует
            }

            throw new KeycloakApiException(
                    "Не удалось удалить пользователя из Keycloak",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (HttpServerErrorException e) {
            log.error("Server error deleting user from Keycloak (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakApiException(
                    "Ошибка сервера Keycloak при удалении пользователя",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (Exception e) {
            log.error("Unexpected error deleting user from Keycloak: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to delete user from Keycloak", 500, null, e);
        }
    }

    /**
     * Получает токен доступа для Keycloak Admin API
     *
     * @return access token
     */
    private String getAdminAccessToken() {
        String url = String.format("%s/realms/master/protocol/openid-connect/token", baseUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    KeycloakTokenResponse.class
            );

            if (response.getBody() != null && response.getBody().getAccessToken() != null) {
                return response.getBody().getAccessToken();
            }

            throw new KeycloakApiException("Failed to get access token from response", 500, null);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error getting admin access token (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakApiException(
                    "Не удалось аутентифицироваться в Keycloak Admin API",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );

        } catch (Exception e) {
            log.error("Unexpected error getting admin access token: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to authenticate with Keycloak Admin API", 500, null, e);
        }
    }

    /**
     * Парсит сообщение об ошибке из ответа Keycloak
     */
    private String parseErrorMessage(String responseBody, String defaultMessage) {
        try {
            if (responseBody != null && !responseBody.isEmpty()) {
                var errorNode = objectMapper.readTree(responseBody);
                if (errorNode.has("errorMessage")) {
                    return errorNode.get("errorMessage").asText();
                }
                if (errorNode.has("error_description")) {
                    return errorNode.get("error_description").asText();
                }
            }
        } catch (Exception e) {
            log.debug("Failed to parse error message from response body", e);
        }
        return defaultMessage;
    }

    /**
     * Получает роль realm по имени
     *
     * @param roleName имя роли
     * @return информация о роли
     */
    public KeycloakRoleDto getRealmRole(String roleName) {
        log.info("Getting realm role: {}", roleName);

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/roles/%s", baseUrl, realm, roleName);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KeycloakRoleDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    KeycloakRoleDto.class
            );

            log.info("Role retrieved successfully: {}", roleName);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Role not found in Keycloak: {}", roleName);
                return null;
            }
            throw new KeycloakApiException(
                    "Не удалось получить роль из Keycloak",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            log.error("Unexpected error getting role from Keycloak: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to get role from Keycloak", 500, null, e);
        }
    }

    /**
     * Назначает роли realm пользователю
     *
     * @param keycloakUserId ID пользователя в Keycloak
     * @param roles          список ролей для назначения
     */
    public void assignRealmRolesToUser(String keycloakUserId, List<KeycloakRoleDto> roles) {
        log.info("Assigning {} realm roles to user: {}", roles.size(), keycloakUserId);

        if (roles.isEmpty()) {
            log.warn("No roles to assign");
            return;
        }

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                baseUrl, realm, keycloakUserId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<List<KeycloakRoleDto>> request = new HttpEntity<>(roles, headers);

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Void.class
            );
            log.info("Roles assigned successfully to user: {}", keycloakUserId);

        } catch (HttpClientErrorException e) {
            log.error("Client error assigning roles (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakApiException(
                    "Не удалось назначить роли пользователю в Keycloak",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            log.error("Unexpected error assigning roles: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to assign roles in Keycloak", 500, null, e);
        }
    }

    /**
     * Удаляет роли realm у пользователя
     *
     * @param keycloakUserId ID пользователя в Keycloak
     * @param roles          список ролей для удаления
     */
    public void removeRealmRolesFromUser(String keycloakUserId, List<KeycloakRoleDto> roles) {
        log.info("Removing {} realm roles from user: {}", roles.size(), keycloakUserId);

        if (roles.isEmpty()) {
            log.warn("No roles to remove");
            return;
        }

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                baseUrl, realm, keycloakUserId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<List<KeycloakRoleDto>> request = new HttpEntity<>(roles, headers);

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
            log.info("Roles removed successfully from user: {}", keycloakUserId);

        } catch (HttpClientErrorException e) {
            log.error("Client error removing roles (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            // Не бросаем исключение, если роли уже удалены
            if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw new KeycloakApiException(
                        "Не удалось удалить роли пользователя в Keycloak",
                        e.getStatusCode().value(),
                        e.getResponseBodyAsString()
                );
            }
        } catch (Exception e) {
            log.error("Unexpected error removing roles: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to remove roles in Keycloak", 500, null, e);
        }
    }

    /**
     * Получает текущие роли realm пользователя
     *
     * @param keycloakUserId ID пользователя в Keycloak
     * @return список ролей пользователя
     */
    public List<KeycloakRoleDto> getUserRealmRoles(String keycloakUserId) {
        log.info("Getting realm roles for user: {}", keycloakUserId);

        String accessToken = getAdminAccessToken();
        String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                baseUrl, realm, keycloakUserId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<KeycloakRoleDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    KeycloakRoleDto[].class
            );

            List<KeycloakRoleDto> roles = response.getBody() != null
                    ? List.of(response.getBody())
                    : List.of();

            log.info("Retrieved {} roles for user: {}", roles.size(), keycloakUserId);
            return roles;

        } catch (HttpClientErrorException e) {
            log.error("Client error getting user roles (status: {}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new KeycloakApiException(
                    "Не удалось получить роли пользователя из Keycloak",
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString()
            );
        } catch (Exception e) {
            log.error("Unexpected error getting user roles: {}", e.getMessage(), e);
            throw new KeycloakApiException("Failed to get user roles from Keycloak", 500, null, e);
        }
    }

    /**
     * Аутентифицирует пользователя в Keycloak и получает токены
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return токены доступа (access_token, refresh_token и др.)
     */
    public KeycloakTokenResponse login(String username, String password) {
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", baseUrl, realm);

        log.info("Attempting login for user: {}", username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    KeycloakTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("User {} successfully authenticated", username);
                return response.getBody();
            } else {
                log.error("Keycloak returned status: {}", response.getStatusCode());
                throw new BadCredentialsException("Authentication failed with status: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Keycloak authentication error for user: {} (status: {})", username, e.getStatusCode());
            throw new BadCredentialsException("Invalid credentials");
        } catch (Exception e) {
            log.error("Unexpected error during authentication for user: {}", username, e);
            throw new BadCredentialsException("Authentication failed");
        }
    }
}