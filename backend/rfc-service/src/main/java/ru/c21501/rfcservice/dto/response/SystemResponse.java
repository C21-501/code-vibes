package ru.c21501.rfcservice.dto.response;

import lombok.Data;

import java.util.UUID;

/**
 * DTO для ответа с данными подсистемы
 */
@Data
public class SystemResponse {
    
    private UUID id;
    
    private String name;
    
    private String type;
    
    private String description;
    
    private TeamResponse responsibleTeam;
}
