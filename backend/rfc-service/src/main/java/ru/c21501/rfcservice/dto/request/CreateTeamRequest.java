package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для создания команды
 */
@Data
public class CreateTeamRequest {
    
    @NotBlank(message = "Название команды не может быть пустым")
    private String name;
    
    private String description;
    
    @NotNull(message = "Руководитель команды не может быть пустым")
    private UUID leaderId;
}
