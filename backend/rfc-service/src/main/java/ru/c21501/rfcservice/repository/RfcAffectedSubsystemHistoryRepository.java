package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemHistoryEntity;

/**
 * Repository для работы с историей изменений затронутых подсистем
 */
@Repository
public interface RfcAffectedSubsystemHistoryRepository extends JpaRepository<RfcAffectedSubsystemHistoryEntity, Long> {
}