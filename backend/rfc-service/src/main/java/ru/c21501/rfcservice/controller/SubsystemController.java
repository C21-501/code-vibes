package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.SubsystemsApi;
import ru.c21501.rfcservice.openapi.model.SubsystemRequest;
import ru.c21501.rfcservice.openapi.model.SubsystemResponse;
import ru.c21501.rfcservice.service.SubsystemApiService;

import java.util.List;

/**
 * Контроллер для работы с подсистемами
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SubsystemController implements SubsystemsApi {

    private final SubsystemApiService subsystemApiService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public SubsystemResponse createSubsystem(Long id, SubsystemRequest subsystemRequest) {
        log.info("POST /api/systems/{}/subsystems - Creating subsystem: {}", id, subsystemRequest.getName());
        return subsystemApiService.createSubsystem(id, subsystemRequest);
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubsystem(Long id, Long subsystemId) {
        log.info("DELETE /api/systems/{}/subsystems/{} - Deleting subsystem", id, subsystemId);
        subsystemApiService.deleteSubsystem(subsystemId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SubsystemResponse getSubsystemById(Long id, Long subsystemId) {
        log.info("GET /api/systems/{}/subsystems/{} - Getting subsystem by ID", id, subsystemId);
        return subsystemApiService.getSubsystemById(subsystemId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<SubsystemResponse> getSystemSubsystems(Long id) {
        log.info("GET /api/systems/{}/subsystems - Getting subsystems for system", id);
        return subsystemApiService.getSubsystemsBySystemId(id);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SubsystemResponse updateSubsystem(Long id, Long subsystemId, SubsystemRequest subsystemRequest) {
        log.info("PUT /api/systems/{}/subsystems/{} - Updating subsystem: {}", id, subsystemId, subsystemRequest.getName());
        return subsystemApiService.updateSubsystem(subsystemId, subsystemRequest);
    }
}