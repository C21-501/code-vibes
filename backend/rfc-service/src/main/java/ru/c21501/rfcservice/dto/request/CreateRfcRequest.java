package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO для создания нового RFC
 */
@Data
public class CreateRfcRequest {
    
    @NotBlank(message = "Заголовок RFC не может быть пустым")
    private String title;
    
    private String description;
    
    @NotNull(message = "Приоритет RFC не может быть пустым")
    private Priority priority;
    
    private LocalDate plannedDate;
    
    @NotNull(message = "Инициатор RFC не может быть пустым")
    private UUID initiatorId;
}
