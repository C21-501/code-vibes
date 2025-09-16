package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с RFC
 */
@Repository
public interface RfcRepository extends JpaRepository<Rfc, String> {
    
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
    List<Rfc> findByInitiatorId(java.util.UUID initiatorId);
    
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
     * Найти максимальный числовой ID для генерации следующего RFC ID
     */
    @Query("SELECT MAX(CAST(SUBSTRING(r.id, 5) AS int)) FROM Rfc r WHERE r.id LIKE 'RFC-%'")
    Integer findMaxNumericId();
    
    /**
     * Найти RFC по заголовку (содержит подстроку, без учета регистра)
     */
    List<Rfc> findByTitleContainingIgnoreCase(String title);
}
