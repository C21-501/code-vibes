package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;

/**
 * DTO для ответа с данными о недавних RFC
 */
@Data
public class RecentRfcResponse {

    private String id;

    private String title;

    private RfcStatus status;

    private Priority priority;

    private LocalDateTime createdDate;

    private String authorName;
}
