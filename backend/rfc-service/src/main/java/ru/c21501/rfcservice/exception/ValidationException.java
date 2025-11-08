package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое при ошибке валидации бизнес-логики
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}