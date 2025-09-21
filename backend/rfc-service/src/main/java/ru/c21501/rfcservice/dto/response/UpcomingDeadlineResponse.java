package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;

/**
 * DTO для ответа с данными о ближайших дедлайнах
 */
@Data
public class UpcomingDeadlineResponse {

    private String id;

    private String title;

    private LocalDateTime deadline;

    private RfcStatus status;
}
