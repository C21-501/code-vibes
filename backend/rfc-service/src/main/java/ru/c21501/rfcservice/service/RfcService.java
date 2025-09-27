package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

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
    Optional<Rfc> findById(UUID id);

    /**
     * Найти RFC по статусу
     */
    List<Rfc> findByStatus(RfcStatus status);

    /**
     * Найти RFC по приоритету
     */
    List<Rfc> findByPriority(Priority priority);

    /**
     * Найти RFC по создателю
     */
    List<Rfc> findByCreatedBy(User createdBy);

    /**
     * Найти RFC по ID создателя
     */
    List<Rfc> findByCreatedById(UUID createdById);

    /**
     * Найти RFC созданные в диапазоне дат
     */
    List<Rfc> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Найти RFC с плановой датой начала в диапазоне
     */
    List<Rfc> findByPlannedStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

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
    boolean existsById(UUID id);

    /**
     * Изменить статус RFC
     */
    Rfc changeStatus(UUID rfcId, RfcStatus newStatus, User changedByUser);

    /**
     * Проверить возможность изменения статуса
     */
    boolean canChangeStatus(UUID rfcId, RfcStatus newStatus, User user);

    /**
     * Проверить, все ли исполнители подтвердили готовность
     */
    boolean areAllExecutorsConfirmed(UUID rfcId);
}
