package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.SubsystemMapper;
import ru.c21501.rfcservice.model.entity.SubsystemEntity;
import ru.c21501.rfcservice.openapi.model.SubsystemRequest;
import ru.c21501.rfcservice.openapi.model.SubsystemResponse;
import ru.c21501.rfcservice.service.SubsystemApiService;
import ru.c21501.rfcservice.service.SubsystemService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация API-сервиса для работы с подсистемами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubsystemApiServiceImpl implements SubsystemApiService {

    private final SubsystemService subsystemService;
    private final SubsystemMapper subsystemMapper;

    @Override
    public SubsystemResponse createSubsystem(Long systemId, SubsystemRequest request) {
        log.info("API: Creating subsystem with name: {} for system ID: {}", request.getName(), systemId);

        // Прямой маппинг: SubsystemRequest -> SubsystemEntity
        SubsystemEntity subsystemEntity = subsystemMapper.toEntity(request);

        // Вызов бизнес-логики с systemId и teamId из request
        SubsystemEntity createdSubsystem = subsystemService.createSubsystem(
                subsystemEntity,
                request.getSystemId(),
                request.getTeamId()
        );

        // Обратный маппинг: SubsystemEntity -> SubsystemResponse
        SubsystemResponse response = subsystemMapper.toResponse(createdSubsystem);

        log.info("API: Subsystem created successfully with ID: {}", response.getId());
        return response;
    }

    @Override
    public SubsystemResponse updateSubsystem(Long id, SubsystemRequest request) {
        log.info("API: Updating subsystem with ID: {}", id);

        // Прямой маппинг: SubsystemRequest -> SubsystemEntity
        SubsystemEntity subsystemEntity = subsystemMapper.toEntity(request);

        // Вызов бизнес-логики
        SubsystemEntity updatedSubsystem = subsystemService.updateSubsystem(
                id,
                subsystemEntity,
                request.getSystemId(),
                request.getTeamId()
        );

        // Обратный маппинг: SubsystemEntity -> SubsystemResponse
        SubsystemResponse response = subsystemMapper.toResponse(updatedSubsystem);

        log.info("API: Subsystem updated successfully: {}", response.getId());
        return response;
    }

    @Override
    public SubsystemResponse getSubsystemById(Long id) {
        log.info("API: Getting subsystem by ID: {}", id);

        // Вызов бизнес-логики
        SubsystemEntity subsystem = subsystemService.getSubsystemById(id);

        // Обратный маппинг: SubsystemEntity -> SubsystemResponse
        SubsystemResponse response = subsystemMapper.toResponse(subsystem);

        log.info("API: Subsystem retrieved successfully: {}", response.getId());
        return response;
    }

    @Override
    public void deleteSubsystem(Long id) {
        log.info("API: Deleting subsystem with ID: {}", id);

        // Вызов бизнес-логики
        subsystemService.deleteSubsystem(id);

        log.info("API: Subsystem deleted successfully: {}", id);
    }

    @Override
    public List<SubsystemResponse> getSubsystemsBySystemId(Long systemId) {
        log.info("API: Getting subsystems for system ID: {}", systemId);

        // Вызов бизнес-логики
        List<SubsystemEntity> subsystems = subsystemService.getSubsystemsBySystemId(systemId);

        // Маппинг в ответ
        List<SubsystemResponse> response = subsystems.stream()
                .map(subsystemMapper::toResponse)
                .collect(Collectors.toList());

        log.info("API: Retrieved {} subsystems for system {}", response.size(), systemId);
        return response;
    }
}