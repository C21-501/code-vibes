package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое когда команда уже существует
 */
public class TeamAlreadyExistsException extends RuntimeException {

    public TeamAlreadyExistsException(String message) {
        super(message);
    }
}