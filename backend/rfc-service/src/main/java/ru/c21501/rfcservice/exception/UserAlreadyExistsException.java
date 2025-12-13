package ru.c21501.rfcservice.exception;

/**
 * Исключение, выбрасываемое когда пользователь уже существует
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}