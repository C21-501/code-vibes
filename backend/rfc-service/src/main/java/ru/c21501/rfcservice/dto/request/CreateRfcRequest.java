package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для создания нового RFC
 */
@Data
public class CreateRfcRequest {

    @NotBlank(message = "Заголовок RFC не может быть пустым")
    private String title;

    @NotBlank(message = "Описание RFC не может быть пустым")
    private String description;

    @NotBlank(message = "Обоснование RFC не может быть пустым")
    private String justification;

    private String impactAnalysis;

    private String rollbackPlan;

    private String testingPlan;

    private String implementationPlan;

    @NotNull(message = "Приоритет RFC не может быть пустым")
    private Priority priority;

    @NotNull(message = "Уровень риска RFC не может быть пустым")
    private RiskLevel riskLevel;

    private Integer estimatedDuration;

    private LocalDateTime plannedStartDate;

    private LocalDateTime plannedEndDate;

    @NotNull(message = "Создатель RFC не может быть пустым")
    private UUID createdById;

    @NotNull(message = "Заявитель RFC не может быть пустым")
    private UUID requesterId;

    private UUID systemId;
}
