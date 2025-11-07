package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcHistoryEntity;

/**
 * Repository для работы с историей изменений RFC
 */
@Repository
public interface RfcHistoryRepository extends JpaRepository<RfcHistoryEntity, Long> {
}