package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemHistoryEntity;

import java.util.List;

/**
 * Repository для работы с историей изменений затронутых подсистем
 */
@Repository
public interface RfcAffectedSubsystemHistoryRepository extends JpaRepository<RfcAffectedSubsystemHistoryEntity, Long> {

    /**
     * Найти историю изменений статусов подсистем для конкретного RFC
     *
     * @param rfcAffectedSubsystemIds список ID связей RFC-подсистема
     * @return список истории изменений статусов
     */
    @Query("SELECT h FROM RfcAffectedSubsystemHistoryEntity h " +
            "LEFT JOIN FETCH h.changedBy " +
            "WHERE h.rfcAffectedSubsystemId IN :rfcAffectedSubsystemIds " +
            "ORDER BY h.createDatetime DESC")
    List<RfcAffectedSubsystemHistoryEntity> findByRfcAffectedSubsystemIdInWithUsers(
            @Param("rfcAffectedSubsystemIds") List<Long> rfcAffectedSubsystemIds
    );
}