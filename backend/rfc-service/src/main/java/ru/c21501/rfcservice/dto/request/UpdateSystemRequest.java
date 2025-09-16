package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для обновления подсистемы
 */
@Data
public class UpdateSystemRequest {
    
    @NotNull(message = "ID подсистемы не может быть пустым")
    private UUID id;
    
    @NotBlank(message = "Название подсистемы не может быть пустым")
    private String name;
    
    private String type;
    
    private String description;
    
    @NotNull(message = "Ответственная команда не может быть пустой")
    private UUID responsibleTeamId;
}
