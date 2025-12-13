package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.RfcMapper;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.AffectedSubsystemResponse;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;
import ru.c21501.rfcservice.openapi.model.UpdateSubsystemConfirmationStatusRequest;
import ru.c21501.rfcservice.openapi.model.UpdateSubsystemExecutionStatusRequest;
import ru.c21501.rfcservice.service.SecurityContextService;
import ru.c21501.rfcservice.service.SubsystemStatusApiService;
import ru.c21501.rfcservice.service.SubsystemStatusService;

/**
 * Реализация API-сервиса для работы со статусами затронутых подсистем
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubsystemStatusApiServiceImpl implements SubsystemStatusApiService {

    private final SubsystemStatusService subsystemStatusService;
    private final SecurityContextService securityContextService;
    private final RfcMapper rfcMapper;

    @Override
    public AffectedSubsystemResponse updateConfirmationStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemConfirmationStatusRequest request
    ) {
        log.info("Updating confirmation status for RFC {} subsystem {} to {}",
                rfcId, subsystemId, request.getStatus());

        UserEntity currentUser = securityContextService.getCurrentUser();

        RfcAffectedSubsystemEntity updated = subsystemStatusService.updateConfirmationStatus(
                rfcId,
                subsystemId,
                ConfirmationStatus.valueOf(request.getStatus().getValue()),
                request.getComment(),
                currentUser
        );

        return rfcMapper.toAffectedSubsystemResponse(updated);
    }

    @Override
    public AffectedSubsystemResponse updateExecutionStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemExecutionStatusRequest request
    ) {
        log.info("Updating execution status for RFC {} subsystem {} to {}",
                rfcId, subsystemId, request.getStatus());

        UserEntity currentUser = securityContextService.getCurrentUser();

        RfcAffectedSubsystemEntity updated = subsystemStatusService.updateExecutionStatus(
                rfcId,
                subsystemId,
                ExecutionStatus.valueOf(request.getStatus().getValue()),
                request.getComment(),
                currentUser
        );

        return rfcMapper.toAffectedSubsystemResponse(updated);
    }
}
