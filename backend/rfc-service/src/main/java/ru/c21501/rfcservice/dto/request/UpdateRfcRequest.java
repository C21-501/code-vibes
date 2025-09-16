package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDate;

/**
 * DTO для обновления RFC
 */
@Data
public class UpdateRfcRequest {
    
    @NotBlank(message = "Заголовок RFC не может быть пустым")
    private String title;
    
    private String description;
    
    private Priority priority;
    
    private RfcStatus status;
    
    private LocalDate plannedDate;
}
