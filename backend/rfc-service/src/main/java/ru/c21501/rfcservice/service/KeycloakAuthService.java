package ru.c21501.rfcservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.c21501.rfcservice.dto.LoginRequest;
import ru.c21501.rfcservice.dto.KeycloakTokenResponse;

@Slf4j
@Service
public class KeycloakAuthService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    public KeycloakAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KeycloakTokenResponse login(LoginRequest loginRequest) {
        String tokenUrl = UriComponentsBuilder.fromHttpUrl(keycloakUrl)
                .path("/realms/{realm}/protocol/openid-connect/token")
                .buildAndExpand(realm)
                .toUriString();

        log.info("Attempting login for user: {}", loginRequest.getUsername());

        // Подготавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Подготавливаем form-data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", loginRequest.getUsername());
        formData.add("password", loginRequest.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    KeycloakTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("User {} successfully authenticated", loginRequest.getUsername());
                return response.getBody();
            } else {
                log.error("Keycloak returned status: {}", response.getStatusCode());
                throw new BadCredentialsException("Authentication failed with status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Keycloak authentication error for user: {}", loginRequest.getUsername(), e);
            throw e;
        }
    }
}
