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
    List<StatusHistory> findByRfcId(UUID rfcId);

    /**
     * Найти историю по RFC, отсортированную по дате изменения
     */
    List<StatusHistory> findByRfcOrderByChangedAtDesc(Rfc rfc);

    /**
     * Найти историю по ID RFC, отсортированную по дате изменения
     */
    List<StatusHistory> findByRfcIdOrderByChangedAtDesc(UUID rfcId);

    /**
     * Найти историю по статусу
     */
    List<StatusHistory> findByStatus(RfcStatus status);

    /**
     * Найти историю по пользователю, который изменил статус
     */
    List<StatusHistory> findByChangedBy(User changedBy);

    /**
     * Найти историю по ID пользователя, который изменил статус
     */
    List<StatusHistory> findByChangedById(UUID changedById);

    /**
     * Найти историю в диапазоне дат
     */
    List<StatusHistory> findByChangedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
