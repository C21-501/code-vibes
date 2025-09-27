package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.c21501.rfcservice.model.enums.RfcStatus;

/**
 * DTO для запроса изменения статуса RFC
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusRequest {

    @NotNull(message = "Новый статус не может быть пустым")
    private RfcStatus newStatus;

    /**
     * Комментарий к изменению статуса (опционально)
     */
    private String comment;
}
