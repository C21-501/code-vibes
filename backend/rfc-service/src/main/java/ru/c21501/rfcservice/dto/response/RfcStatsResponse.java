package ru.c21501.rfcservice.dto.response;

import lombok.Data;

/**
 * DTO для ответа со статистикой RFC
 */
@Data
public class RfcStatsResponse {
    
    private Long total;
    
    private Long draft;
    
    private Long review;
    
    private Long approved;
    
    private Long implemented;
    
    private Long rejected;
}
