package ru.c21501.rfcservice.service;

/**
 * Сервис для автоматического обновления статусов RFC
 */
public interface RfcStatusSchedulerService {

    /**
     * Обновляет статусы всех RFC на основе состояния подсистем и аппрувов
     */
    void updateRfcStatuses();
}