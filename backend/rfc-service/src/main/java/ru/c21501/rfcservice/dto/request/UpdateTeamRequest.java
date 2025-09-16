package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для обновления команды
 */
@Data
public class UpdateTeamRequest {
    
    @NotNull(message = "ID команды не может быть пустым")
    private UUID id;
    
    @NotBlank(message = "Название команды не может быть пустым")
    private String name;
    
    private String description;
    
    @NotNull(message = "Руководитель команды не может быть пустым")
    private UUID leaderId;
}
