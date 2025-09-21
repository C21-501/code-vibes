package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.model.enums.RiskLevel;

import java.time.LocalDateTime;

/**
 * DTO для обновления RFC
 */
@Data
public class UpdateRfcRequest {

    @NotBlank(message = "Заголовок RFC не может быть пустым")
    private String title;

    private String description;

    private String justification;

    private String impactAnalysis;

    private String rollbackPlan;

    private String testingPlan;

    private String implementationPlan;

    private Priority priority;

    private RiskLevel riskLevel;

    private RfcStatus status;

    private Integer estimatedDuration;

    private LocalDateTime plannedStartDate;

    private LocalDateTime plannedEndDate;
}
