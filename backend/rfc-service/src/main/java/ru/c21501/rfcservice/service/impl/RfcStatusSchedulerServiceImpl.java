package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.UserRole;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;
import ru.c21501.rfcservice.openapi.model.RfcStatus;
import ru.c21501.rfcservice.repository.RfcApprovalRepository;
import ru.c21501.rfcservice.repository.RfcRepository;
import ru.c21501.rfcservice.repository.UserRepository;
import ru.c21501.rfcservice.service.PlankaIntegrationService;
import ru.c21501.rfcservice.service.RfcStatusSchedulerService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Реализация сервиса для автоматического обновления статусов RFC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfcStatusSchedulerServiceImpl implements RfcStatusSchedulerService {

    private final RfcRepository rfcRepository;
    private final RfcApprovalRepository rfcApprovalRepository;
    private final UserRepository userRepository;
    private final PlankaIntegrationService plankaIntegrationService;

    /**
     * Обновляет статусы RFC каждые 3 секунды
     */
    @Override
    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void updateRfcStatuses() {
        log.debug("Starting RFC status update job");

        List<RfcEntity> allRfcs = rfcRepository.findAll();

        for (RfcEntity rfc : allRfcs) {
            try {
                // Пропускаем RFC с "финальными" статусами (изменены вручную)
                // Эти статусы могут быть установлены только через Planka или вручную
                if (rfc.getStatus() == RfcStatus.REJECTED || rfc.getStatus() == RfcStatus.IMPLEMENTED) {
                    log.debug("Skipping RFC {} with final status: {}", rfc.getId(), rfc.getStatus());
                    continue;
                }
                
                RfcStatus newStatus = calculateRfcStatus(rfc);

                if (newStatus != rfc.getStatus()) {
                    log.info("Updating RFC {} status from {} to {}", rfc.getId(), rfc.getStatus(), newStatus);
                    rfc.setStatus(newStatus);
                    rfc.setUpdateDatetime(OffsetDateTime.now());
                    rfcRepository.save(rfc);
                    
                    // Синхронизация с Planka - перемещение карточки в соответствующий список
                    if (rfc.getPlankaCardId() != null) {
                        try {
                            plankaIntegrationService.updateRfcStatusInPlanka(rfc, rfc.getPlankaCardId());
                            log.info("RFC {} card moved in Planka to status list: {}", rfc.getId(), newStatus);
                        } catch (Exception e) {
                            log.warn("Failed to sync RFC {} status to Planka: {}", rfc.getId(), e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error updating status for RFC {}: {}", rfc.getId(), e.getMessage(), e);
            }
        }

        log.debug("RFC status update job completed");
    }

    /**
     * Вычисляет новый статус RFC на основе состояния подсистем и аппрувов
     *
     * Логика:
     * 1. Если хотя бы одна подсистема отклонена (ConfirmationStatus.REJECTED) → REJECTED
     * 2. Если хотя бы одна подсистема ожидает подтверждения (ConfirmationStatus.PENDING) → UNDER_REVIEW
     * 3. Если все RFC_APPROVER согласовали И не все подсистемы выполнены → APPROVED
     * 4. Если все RFC_APPROVER согласовали И все подсистемы выполнены → IMPLEMENTED
     * 5. Иначе → NEW
     */
    private RfcStatus calculateRfcStatus(RfcEntity rfc) {
        List<RfcAffectedSubsystemEntity> subsystems = rfc.getAffectedSubsystems();

        // Правило 1: Если хотя бы одна подсистема отклонена → REJECTED
        boolean anySubsystemRejected = subsystems.stream()
                .anyMatch(sub -> sub.getConfirmationStatus() == ConfirmationStatus.REJECTED);

        if (anySubsystemRejected) {
            return RfcStatus.REJECTED;
        }

        // Правило 2: Если хотя бы одна подсистема ожидает подтверждения → NEW
        boolean anySubsystemPending = subsystems.stream()
                .anyMatch(sub -> sub.getConfirmationStatus() == ConfirmationStatus.PENDING);

        if (anySubsystemPending) {
            return RfcStatus.NEW;
        }

        // Получаем всех пользователей с ролью RFC_APPROVER
        List<UserEntity> allApprovers = userRepository.findByRole(UserRole.RFC_APPROVER);

        // Если есть аппруверы, проверяем их согласования
        if (!allApprovers.isEmpty()) {
            List<RfcApprovalEntity> approvals = rfcApprovalRepository.findByRfcId(rfc.getId());

            // Проверяем, согласовали ли все RFC_APPROVER
            boolean allApproversApproved = allApprovers.stream()
                    .allMatch(approver -> approvals.stream()
                            .anyMatch(approval ->
                                    approval.getApprover().getId().equals(approver.getId()) &&
                                    approval.getIsApproved()
                            )
                    );

            if (allApproversApproved) {
                // Проверяем статус выполнения всех подсистем
                boolean allSubsystemsDone = subsystems.stream()
                        .allMatch(sub -> sub.getExecutionStatus() == ExecutionStatus.DONE);

                // Правило 4: Все аппруверы согласовали И все подсистемы выполнены → IMPLEMENTED
                if (allSubsystemsDone) {
                    return RfcStatus.IMPLEMENTED;
                }

                // Правило 3: Все аппруверы согласовали И не все подсистемы выполнены → APPROVED
                return RfcStatus.APPROVED;
            }
        }

        // Правило 5: Иначе → UNDER_REVIEW
        return RfcStatus.UNDER_REVIEW;
    }
}