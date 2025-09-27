package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.c21501.rfcservice.model.enums.UserRole;

import java.util.UUID;

/**
 * Сущность пользователя системы
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    @NotBlank(message = "Keycloak ID не может быть пустым")
    private String keycloakId;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "Роль пользователя не может быть пустой")
    private UserRole role;
}
