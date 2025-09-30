package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.UserRole;

import java.util.UUID;

/**
 * DTO для ответа с данными пользователя
 */
@Data
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String fullName;

    private UserRole role;
}
