package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ForbiddenException;
import ru.c21501.rfcservice.exception.NotFoundException;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.UserRole;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;
import ru.c21501.rfcservice.repository.RfcAffectedSubsystemRepository;
import ru.c21501.rfcservice.service.SubsystemStatusService;
import ru.c21501.rfcservice.validator.SubsystemStatusValidator;

/**
 * Реализация сервиса для работы со статусами затронутых подсистем
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubsystemStatusServiceImpl implements SubsystemStatusService {

    private final RfcAffectedSubsystemRepository affectedSubsystemRepository;
    private final SubsystemStatusValidator statusValidator;

    @Override
    @Transactional
    public RfcAffectedSubsystemEntity updateConfirmationStatus(
            Long rfcId,
            Long subsystemId,
            ConfirmationStatus newStatus,
            String comment,
            UserEntity currentUser
    ) {
        log.info("Updating confirmation status for RFC {} subsystem {} to {}", rfcId, subsystemId, newStatus);

        RfcAffectedSubsystemEntity affectedSubsystem = findAffectedSubsystem(rfcId, subsystemId);

        // Проверка прав доступа
        validateAccess(affectedSubsystem, currentUser);

        // Валидация перехода статуса
        statusValidator.validateConfirmationStatusTransition(
                affectedSubsystem.getConfirmationStatus(),
                newStatus
        );

        // Обновление статуса
        affectedSubsystem.setConfirmationStatus(newStatus);

        RfcAffectedSubsystemEntity saved = affectedSubsystemRepository.save(affectedSubsystem);
        log.info("Confirmation status updated successfully for subsystem {}", subsystemId);

        return saved;
    }

    @Override
    @Transactional
    public RfcAffectedSubsystemEntity updateExecutionStatus(
            Long rfcId,
            Long subsystemId,
            ExecutionStatus newStatus,
            String comment,
            UserEntity currentUser
    ) {
        log.info("Updating execution status for RFC {} subsystem {} to {}", rfcId, subsystemId, newStatus);

        RfcAffectedSubsystemEntity affectedSubsystem = findAffectedSubsystem(rfcId, subsystemId);

        // Проверка прав доступа
        validateAccess(affectedSubsystem, currentUser);

        // Валидация перехода статуса
        statusValidator.validateExecutionStatusTransition(
                affectedSubsystem.getExecutionStatus(),
                newStatus
        );

        // Обновление статуса
        affectedSubsystem.setExecutionStatus(newStatus);

        RfcAffectedSubsystemEntity saved = affectedSubsystemRepository.save(affectedSubsystem);
        log.info("Execution status updated successfully for subsystem {}", subsystemId);

        return saved;
    }

    private RfcAffectedSubsystemEntity findAffectedSubsystem(Long rfcId, Long subsystemId) {
        return affectedSubsystemRepository.findByIdAndRfcId(subsystemId, rfcId)
                .orElseThrow(() -> {
                    log.warn("Affected subsystem not found: rfcId={}, subsystemId={}", rfcId, subsystemId);
                    return new NotFoundException("Затронутая подсистема не найдена");
                });
    }

    /**
     * Проверяет, имеет ли пользователь право изменять статусы подсистемы
     * Правило: только ответственный этой подсистемы или ADMIN
     */
    private void validateAccess(RfcAffectedSubsystemEntity affectedSubsystem, UserEntity currentUser) {
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        boolean isExecutor = affectedSubsystem.getExecutor().getId().equals(currentUser.getId());

        if (!isAdmin && !isExecutor) {
            log.warn("User {} has no access to update subsystem {} status",
                    currentUser.getId(), affectedSubsystem.getId());
            throw new ForbiddenException("Недостаточно прав для изменения статуса подсистемы");
        }
    }
}