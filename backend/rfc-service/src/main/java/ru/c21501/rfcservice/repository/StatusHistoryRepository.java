package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.StatusHistory;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с историей статусов RFC
 */
@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, UUID> {
    
    /**
     * Найти историю по RFC
     */
    List<StatusHistory> findByRfc(Rfc rfc);
    
    /**
     * Найти историю по ID RFC
     */
    List<StatusHistory> findByRfcId(String rfcId);
    
    /**
     * Найти историю по RFC, отсортированную по дате изменения
     */
    List<StatusHistory> findByRfcOrderByChangeDateDesc(Rfc rfc);
    
    /**
     * Найти историю по ID RFC, отсортированную по дате изменения
     */
    List<StatusHistory> findByRfcIdOrderByChangeDateDesc(String rfcId);
    
    /**
     * Найти историю по старому статусу
     */
    List<StatusHistory> findByOldStatus(RfcStatus oldStatus);
    
    /**
     * Найти историю по новому статусу
     */
    List<StatusHistory> findByNewStatus(RfcStatus newStatus);
    
    /**
     * Найти историю по пользователю, который изменил статус
     */
    List<StatusHistory> findByChangedByUser(User changedByUser);
    
    /**
     * Найти историю по ID пользователя, который изменил статус
     */
    List<StatusHistory> findByChangedByUserId(UUID changedByUserId);
    
    /**
     * Найти историю в диапазоне дат
     */
    List<StatusHistory> findByChangeDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
