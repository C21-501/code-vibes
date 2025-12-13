package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface RfcApprovalRepository extends JpaRepository<RfcApprovalEntity, Long> {

    List<RfcApprovalEntity> findByRfcId(Long rfcId);

    Optional<RfcApprovalEntity> findByRfcIdAndApproverId(Long rfcId, Long approverId);

    long countByRfcIdAndIsApprovedTrue(Long rfcId);

    boolean existsByRfcIdAndApproverIdAndIsApprovedFalse(Long rfcId, Long approverId);
}