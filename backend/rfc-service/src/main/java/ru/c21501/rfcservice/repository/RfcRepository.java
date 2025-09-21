package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с RFC
 */
@Repository
public interface RfcRepository extends JpaRepository<Rfc, java.util.UUID>, JpaSpecificationExecutor<Rfc> {

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
    List<Rfc> findByCreatedById(java.util.UUID createdById);

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
    List<Rfc> findByTitleContainingIgnoreCase(String title);

    /**
     * Подсчитать количество RFC по статусу
     */
    Long countByStatus(RfcStatus status);

    /**
     * Найти последние N RFC, отсортированные по дате создания
     */
    List<Rfc> findTop10ByOrderByCreatedAtDesc();

    /**
     * Найти RFC с ближайшими дедлайнами (плановая дата начала в будущем)
     */
    @Query("SELECT r FROM Rfc r WHERE r.plannedStartDate >= CURRENT_DATE AND r.status NOT IN ('IMPLEMENTED', 'CANCELLED') ORDER BY r.plannedStartDate ASC")
    List<Rfc> findUpcomingDeadlines();
}
