package ru.c21501.rfcservice.dto.response;

import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными RFC
 */
@Data
public class RfcResponse {

    private UUID id;

    private String title;

    private String description;

    private String justification;

    private String impactAnalysis;

    private String rollbackPlan;

    private String testingPlan;

    private String implementationPlan;

    private RfcStatus status;

    private Priority priority;

    private RiskLevel riskLevel;

    private Integer estimatedDuration;

    private Integer actualDuration;

    private LocalDateTime plannedStartDate;

    private LocalDateTime plannedEndDate;

    private LocalDateTime actualStartDate;

    private LocalDateTime actualEndDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime implementedAt;

    private UserResponse createdBy;

    private UserResponse requester;

    private SystemResponse system;
}
