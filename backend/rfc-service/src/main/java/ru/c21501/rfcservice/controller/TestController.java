package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.dto.LoginRequest;
import ru.c21501.rfcservice.dto.KeycloakTokenResponse;
import ru.c21501.rfcservice.service.KeycloakAuthService;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final KeycloakAuthService keycloakAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login request for user: {}", loginRequest.getUsername());

            KeycloakTokenResponse tokenResponse = keycloakAuthService.login(loginRequest);

            log.info("User {} successfully authenticated", loginRequest.getUsername());

            return ResponseEntity.ok(tokenResponse);

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: " + e.getMessage());
        }
    }
}