package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое когда ресурс не найден
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}