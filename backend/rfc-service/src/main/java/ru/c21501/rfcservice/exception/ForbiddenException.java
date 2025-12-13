package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое при отсутствии прав доступа
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}