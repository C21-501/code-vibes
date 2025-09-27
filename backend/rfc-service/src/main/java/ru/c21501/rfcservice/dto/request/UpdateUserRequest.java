package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.c21501.rfcservice.model.enums.UserRole;

import java.util.UUID;

/**
 * DTO для обновления пользователя
 */
@Data
public class UpdateUserRequest {

    @NotNull(message = "ID пользователя не может быть пустым")
    private UUID id;

    @NotBlank(message = "Keycloak ID не может быть пустым")
    private String keycloakId;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @NotNull(message = "Роль пользователя не может быть пустой")
    private UserRole role;
}
