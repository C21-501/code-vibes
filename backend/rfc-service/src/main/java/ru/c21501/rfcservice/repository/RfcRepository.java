package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcEntity;

import java.util.Optional;

/**
 * Repository для работы с RFC
 */
@Repository
public interface RfcRepository extends JpaRepository<RfcEntity, Long>, JpaSpecificationExecutor<RfcEntity> {
    
    /**
     * Найти RFC по ID карточки в Planka
     */
    Optional<RfcEntity> findByPlankaCardId(String plankaCardId);
}