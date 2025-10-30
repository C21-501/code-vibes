package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.RfcApi;
import ru.c21501.rfcservice.openapi.model.*;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class RfcController implements RfcApi {

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public RfcResponse createRfc(RfcRequest rfcRequest) {
        log.info("createRfc called with: {}", rfcRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRfc(Long id) {
        log.info("deleteRfc called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public RfcResponse getRfcById(Long id) {
        log.info("getRfcById called with id: {}", id);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public RfcPageResponse getRfcs(Integer page, Integer size, String status, String urgency, Long requesterId) {
        log.info("getRfcs called with page: {}, size: {}, status: {}, urgency: {}, requesterId: {}",
                 page, size, status, urgency, requesterId);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public RfcResponse updateRfc(Long id, RfcRequest rfcRequest) {
        log.info("updateRfc called with id: {}, rfcRequest: {}", id, rfcRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public AffectedSubsystemResponse updateSubsystemConfirmationStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemConfirmationStatusRequest updateSubsystemConfirmationStatusRequest) {
        log.info("updateSubsystemConfirmationStatus called with rfcId: {}, subsystemId: {}, request: {}",
                 rfcId, subsystemId, updateSubsystemConfirmationStatusRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public AffectedSubsystemResponse updateSubsystemExecutionStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemExecutionStatusRequest updateSubsystemExecutionStatusRequest) {
        log.info("updateSubsystemExecutionStatus called with rfcId: {}, subsystemId: {}, request: {}",
                 rfcId, subsystemId, updateSubsystemExecutionStatusRequest);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}