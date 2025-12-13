package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое когда система уже существует
 */
public class SystemAlreadyExistsException extends RuntimeException {

    public SystemAlreadyExistsException(String message) {
        super(message);
    }
}