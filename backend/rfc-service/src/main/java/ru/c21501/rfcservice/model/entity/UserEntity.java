package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.c21501.rfcservice.model.enums.UserRole;

import java.time.OffsetDateTime;

/**
 * Сущность пользователя системы
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "id")
public class UserEntity {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя пользователя (уникальное)
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Имя пользователя
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Фамилия пользователя
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Роль пользователя в системе
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private UserRole role;

    /**
     * ID пользователя в Keycloak
     */
    @Column(name = "keycloak_id", unique = true, length = 255)
    private String keycloakId;

    /**
     * ID пользователя в Planka (для синхронизации SSO)
     */
    @Column(name = "planka_user_id", unique = true, length = 255)
    private String plankaUserId;

    /**
     * Email пользователя (синхронизируется с Keycloak)
     */
    @Column(name = "email", unique = true, length = 255)
    private String email;

    /**
     * Дата и время создания записи
     */
    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;

    /**
     * Дата и время последнего обновления записи
     */
    @UpdateTimestamp
    @Column(name = "update_datetime", nullable = false)
    private OffsetDateTime updateDatetime;

    /**
     * Получить полное имя пользователя
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}