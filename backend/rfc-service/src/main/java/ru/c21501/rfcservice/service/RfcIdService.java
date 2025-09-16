package ru.c21501.rfcservice.service;

/**
 * Сервис для генерации ID RFC
 */
public interface RfcIdService {
    
    /**
     * Генерирует следующий ID для RFC в формате "RFC-XXXX"
     * Находит максимальный числовой ID в БД и генерирует следующий
     * 
     * @return новый ID RFC в формате "RFC-XXXX"
     */
    String generateNextRfcId();
}
