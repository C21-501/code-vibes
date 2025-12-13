package ru.c21501.rfcservice.exception;

import lombok.Getter;

/**
 * Исключение для ошибок Keycloak API
 */
@Getter
public class KeycloakApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public KeycloakApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public KeycloakApiException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}