package ru.c21501.rfcservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для роли Keycloak
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakRoleDto {

    private String id;
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;
}