package ru.c21501.rfcservice.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для учетных данных пользователя Keycloak
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakCredentialDto {

    private String type;

    @JsonProperty("value")
    private String value;

    @JsonProperty("temporary")
    private Boolean temporary;

    public static KeycloakCredentialDto password(String password, boolean temporary) {
        return KeycloakCredentialDto.builder()
                .type("password")
                .value(password)
                .temporary(temporary)
                .build();
    }
}