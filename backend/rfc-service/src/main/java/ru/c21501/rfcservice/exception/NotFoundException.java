package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое когда ресурс не найден
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}