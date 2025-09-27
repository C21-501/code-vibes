package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными истории статусов
 */
@Data
public class StatusHistoryResponse {

    private UUID id;

    private RfcStatus oldStatus;

    private RfcStatus newStatus;

    private UserResponse changedByUser;

    private LocalDateTime changeDate;
}
