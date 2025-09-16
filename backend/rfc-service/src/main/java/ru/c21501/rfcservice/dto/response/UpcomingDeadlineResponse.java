package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDate;

/**
 * DTO для ответа с данными о ближайших дедлайнах
 */
@Data
public class UpcomingDeadlineResponse {
    
    private String id;
    
    private String title;
    
    private LocalDate deadline;
    
    private RfcStatus status;
}
