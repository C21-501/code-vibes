package ru.c21501.rfcservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.c21501.rfcservice.openapi.model.Error;
import ru.c21501.rfcservice.openapi.model.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений
 */
@Slf4j
@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());

        Error error = new Error()
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());

        Error error = new Error()
                .code("USER_ALREADY_EXISTS")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(KeycloakApiException.class)
    public ResponseEntity<ErrorResponse> handleKeycloakApiException(KeycloakApiException ex) {
        log.error("Keycloak API error (status: {}): {}", ex.getStatusCode(), ex.getMessage());

        Error error = new Error()
                .code("KEYCLOAK_API_ERROR")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        // Пробрасываем статус код из Keycloak или используем 500 по умолчанию
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Bad request: {}", ex.getMessage());

        Error error = new Error()
                .code("BAD_REQUEST")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Authentication failed: {}", ex.getMessage());

        Error error = new Error()
                .code("INVALID_CREDENTIALS")
                .message("Invalid username or password");

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation failed: {}", ex.getMessage());

        List<Error> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = error instanceof FieldError
                            ? ((FieldError) error).getField()
                            : error.getObjectName();
                    String message = error.getDefaultMessage();

                    return new Error()
                            .code("VALIDATION_ERROR")
                            .message(String.format("%s: %s", fieldName, message));
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);

        Error error = new Error()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred");

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}