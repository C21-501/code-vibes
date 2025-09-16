package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными RFC
 */
@Data
public class RfcResponse {
    
    private String id;
    
    private String title;
    
    private String description;
    
    private RfcStatus status;
    
    private Priority priority;
    
    private LocalDate plannedDate;
    
    private LocalDateTime createdDate;
    
    private UserResponse initiator;
}
