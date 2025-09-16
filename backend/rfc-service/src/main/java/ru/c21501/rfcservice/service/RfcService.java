package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с RFC
 */
public interface RfcService {
    
    /**
     * Создать новый RFC
     */
    Rfc createRfc(Rfc rfc);
    
    /**
     * Обновить RFC
     */
    Rfc updateRfc(Rfc rfc);
    
    /**
     * Найти RFC по ID
     */
    Optional<Rfc> findById(String id);
    
    /**
     * Найти RFC по статусу
     */
    List<Rfc> findByStatus(RfcStatus status);
    
    /**
     * Найти RFC по приоритету
     */
    List<Rfc> findByPriority(Priority priority);
    
    /**
     * Найти RFC по инициатору
     */
    List<Rfc> findByInitiator(User initiator);
    
    /**
     * Найти RFC по ID инициатора
     */
    List<Rfc> findByInitiatorId(UUID initiatorId);
    
    /**
     * Найти RFC созданные в диапазоне дат
     */
    List<Rfc> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Найти RFC с плановой датой в диапазоне
     */
    List<Rfc> findByPlannedDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Найти RFC по статусу и приоритету
     */
    List<Rfc> findByStatusAndPriority(RfcStatus status, Priority priority);
    
    /**
     * Найти RFC по заголовку (содержит подстроку, без учета регистра)
     */
    List<Rfc> findByTitleContaining(String title);
    
    /**
     * Получить все RFC
     */
    List<Rfc> findAll();
    
    /**
     * Проверить существование RFC по ID
     */
    boolean existsById(String id);
}
