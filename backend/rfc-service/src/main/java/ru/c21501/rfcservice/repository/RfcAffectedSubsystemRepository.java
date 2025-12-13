package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;

import java.util.Optional;

/**
 * Repository для работы с затронутыми подсистемами RFC
 */
@Repository
public interface RfcAffectedSubsystemRepository extends JpaRepository<RfcAffectedSubsystemEntity, Long> {

    @Query("SELECT ras FROM RfcAffectedSubsystemEntity ras WHERE ras.subsystem.id = :id AND ras.rfc.id = :rfcId")
    Optional<RfcAffectedSubsystemEntity> findBySubsystemIdAndRfcId(@Param("id") Long id, @Param("rfcId") Long rfcId);
}