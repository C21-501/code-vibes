package ru.c21501.rfcservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO для создания подсистемы
 */
@Data
public class CreateSystemRequest {

    @NotBlank(message = "Название подсистемы не может быть пустым")
    private String name;

    private String type;

    private String description;

    @NotNull(message = "Ответственная команда не может быть пустой")
    private UUID responsibleTeamId;
}
