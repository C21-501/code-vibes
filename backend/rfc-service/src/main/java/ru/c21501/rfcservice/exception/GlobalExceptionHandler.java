package ru.c21501.rfcservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.error("Not found: {}", ex.getMessage());

        Error error = new Error()
                .code("NOT_FOUND")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        log.error("Access denied: {}", ex.getMessage());

        Error error = new Error()
                .code("FORBIDDEN")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.error("Validation error: {}", ex.getMessage());

        Error error = new Error()
                .code("VALIDATION_ERROR")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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

    @ExceptionHandler(TeamAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTeamAlreadyExistsException(TeamAlreadyExistsException ex) {
        log.error("Team already exists: {}", ex.getMessage());

        Error error = new Error()
                .code("TEAM_ALREADY_EXISTS")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(SystemAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSystemAlreadyExistsException(SystemAlreadyExistsException ex) {
        log.error("System already exists: {}", ex.getMessage());

        Error error = new Error()
                .code("SYSTEM_ALREADY_EXISTS")
                .message(ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(SubsystemAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSubsystemAlreadyExistsException(SubsystemAlreadyExistsException ex) {
        log.error("Subsystem already exists: {}", ex.getMessage());

        Error error = new Error()
                .code("SUBSYSTEM_ALREADY_EXISTS")
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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("File size limit exceeded: {}", ex.getMessage());

        Error error = new Error()
                .code("FILE_SIZE_LIMIT_EXCEEDED")
                .message("File size exceeds maximum limit of 5MB");

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Malformed JSON request: {}", ex.getMessage());

        String message = "Invalid request body format";

        // Пытаемся извлечь более детальную информацию об ошибке
        if (ex.getCause() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage != null) {
                // Упрощаем сообщение для пользователя
                if (causeMessage.contains("Cannot construct instance")) {
                    message = "Invalid request format. Please check the structure of your request body.";
                } else if (causeMessage.contains("Cannot deserialize")) {
                    message = "Cannot parse request data. Please verify the data types in your request.";
                } else {
                    message = "Malformed JSON request: " + causeMessage;
                }
            }
        }

        Error error = new Error()
                .code("INVALID_REQUEST_BODY")
                .message(message);

        ErrorResponse errorResponse = new ErrorResponse()
                .errors(List.of(error));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
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