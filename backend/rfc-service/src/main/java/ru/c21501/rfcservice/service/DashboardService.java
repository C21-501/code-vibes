package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.dto.response.DashboardResponse;

/**
 * Сервис для работы с данными дашборда
 */
public interface DashboardService {
    
    /**
     * Получить данные для дашборда
     *
     * @return данные дашборда
     */
    DashboardResponse getDashboardData();
}
