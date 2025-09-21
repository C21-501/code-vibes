package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.StatusHistory;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с историей статусов RFC
 */
public interface StatusHistoryService {

    /**
     * Создать новую запись истории
     */
    StatusHistory createStatusHistory(StatusHistory statusHistory);

    /**
     * Обновить запись истории
     */
    StatusHistory updateStatusHistory(StatusHistory statusHistory);

    /**
     * Найти запись истории по ID
     */
    Optional<StatusHistory> findById(UUID id);

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
    List<StatusHistory> findByRfcIdOrderByChangeDateDesc(UUID rfcId);

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

    /**
     * Получить всю историю
     */
    List<StatusHistory> findAll();

    /**
     * Проверить существование записи истории по ID
     */
    boolean existsById(UUID id);
}
