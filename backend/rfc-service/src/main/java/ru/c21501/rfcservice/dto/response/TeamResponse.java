package ru.c21501.rfcservice.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * DTO для ответа с данными команды
 */
@Data
public class TeamResponse {

    private UUID id;

    private String name;

    private String description;

    private UserResponse leader;
}
