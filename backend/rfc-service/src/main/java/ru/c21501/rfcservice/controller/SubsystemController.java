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

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class SubsystemController implements SubsystemsApi {

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public SubsystemResponse createSubsystem(Long id, SubsystemRequest subsystemRequest) {
        log.info("createSubsystem called with systemId: {}, subsystemRequest: {}", id, subsystemRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubsystem(Long id, Long subsystemId) {
        log.info("deleteSubsystem called with systemId: {}, subsystemId: {}", id, subsystemId);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SubsystemResponse getSubsystemById(Long id, Long subsystemId) {
        log.info("getSubsystemById called with systemId: {}, subsystemId: {}", id, subsystemId);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<SubsystemResponse> getSystemSubsystems(Long id) {
        log.info("getSystemSubsystems called with systemId: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public SubsystemResponse updateSubsystem(Long id, Long subsystemId, SubsystemRequest subsystemRequest) {
        log.info("updateSubsystem called with systemId: {}, subsystemId: {}, subsystemRequest: {}",
                id, subsystemId, subsystemRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}