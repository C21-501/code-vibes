package ru.c21501.rfcservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для создания/обновления пользователя в Keycloak
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserDto {

    private String id;

    private String username;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    private String email;

    private Boolean enabled;

    private Boolean emailVerified;

    private List<KeycloakCredentialDto> credentials;
}