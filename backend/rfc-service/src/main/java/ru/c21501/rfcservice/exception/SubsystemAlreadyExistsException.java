package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое когда подсистема уже существует
 */
public class SubsystemAlreadyExistsException extends RuntimeException {

    public SubsystemAlreadyExistsException(String message) {
        super(message);
    }
}