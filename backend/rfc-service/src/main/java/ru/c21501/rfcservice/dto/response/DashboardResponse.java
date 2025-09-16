package ru.c21501.rfcservice.dto.response;

import lombok.Data;

import java.util.List;

/**
 * DTO для ответа с данными дашборда
 */
@Data
public class DashboardResponse {
    
    private RfcStatsResponse stats;
    
    private List<RecentRfcResponse> recentRfcs;
    
    private List<UpcomingDeadlineResponse> upcomingDeadlines;
}
