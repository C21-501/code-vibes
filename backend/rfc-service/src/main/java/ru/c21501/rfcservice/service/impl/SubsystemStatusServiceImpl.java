package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.c21501.rfcservice.exception.ForbiddenException;
import ru.c21501.rfcservice.exception.NotFoundException;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemHistoryEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;
import ru.c21501.rfcservice.model.enums.UserRole;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;
import ru.c21501.rfcservice.repository.RfcAffectedSubsystemHistoryRepository;
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
    private final RfcAffectedSubsystemHistoryRepository historyRepository;
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

        // Сохраняем старый статус для истории
        ConfirmationStatus oldStatus = affectedSubsystem.getConfirmationStatus();

        // Валидация перехода статуса
        statusValidator.validateConfirmationStatusTransition(oldStatus, newStatus);

        // Обновление статуса
        affectedSubsystem.setConfirmationStatus(newStatus);

        RfcAffectedSubsystemEntity saved = affectedSubsystemRepository.save(affectedSubsystem);

        // Создание записи в истории
        createHistoryRecord(
                saved.getId(),
                "CONFIRMATION",
                oldStatus.name(),
                newStatus.name(),
                currentUser
        );

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

        // Сохраняем старый статус для истории
        ExecutionStatus oldStatus = affectedSubsystem.getExecutionStatus();

        // Валидация перехода статуса
        statusValidator.validateExecutionStatusTransition(oldStatus, newStatus);

        // Обновление статуса
        affectedSubsystem.setExecutionStatus(newStatus);

        RfcAffectedSubsystemEntity saved = affectedSubsystemRepository.save(affectedSubsystem);

        // Создание записи в истории
        createHistoryRecord(
                saved.getId(),
                "EXECUTION",
                oldStatus.name(),
                newStatus.name(),
                currentUser
        );

        log.info("Execution status updated successfully for subsystem {}", subsystemId);

        return saved;
    }

    private RfcAffectedSubsystemEntity findAffectedSubsystem(Long rfcId, Long subsystemId) {
        return affectedSubsystemRepository.findBySubsystemIdAndRfcId(subsystemId, rfcId)
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

    /**
     * Создает запись в истории изменений статусов подсистемы
     */
    private void createHistoryRecord(
            Long affectedSubsystemId,
            String statusType,
            String oldStatus,
            String newStatus,
            UserEntity changedBy
    ) {
        RfcAffectedSubsystemHistoryEntity historyRecord = RfcAffectedSubsystemHistoryEntity.builder()
                .rfcAffectedSubsystemId(affectedSubsystemId)
                .operation(HistoryOperationType.UPDATE)
                .statusType(statusType)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .build();

        historyRepository.save(historyRecord);
        log.debug("Created history record for subsystem {} status change: {} {} -> {}",
                affectedSubsystemId, statusType, oldStatus, newStatus);
    }
}