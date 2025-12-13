package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.openapi.api.RfcApi;
import ru.c21501.rfcservice.openapi.model.*;
import ru.c21501.rfcservice.service.RfcApiService;
import ru.c21501.rfcservice.service.RfcApprovalApiService;
import ru.c21501.rfcservice.service.RfcHistoryService;
import ru.c21501.rfcservice.service.SubsystemStatusApiService;

/**
 * Контроллер для работы с RFC
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RfcController implements RfcApi {

    private final RfcApiService rfcApiService;
    private final SubsystemStatusApiService subsystemStatusApiService;
    private final RfcApprovalApiService rfcApprovalApiService;
    private final RfcHistoryService rfcHistoryService;

    @Override
    public RfcResponse createRfc(RfcRequest rfcRequest) {
        log.info("POST /api/rfc - Creating RFC: {}", rfcRequest.getTitle());
        return rfcApiService.createRfc(rfcRequest);
    }

    @Override
    public RfcPageResponse getRfcs(Integer page, Integer size, String status, String urgency, Long requesterId, String title) {
        log.info(
                "GET /api/rfc - Getting RFC list with filters: " +
                        "status={}, urgency={}, requesterId={}, title={}, page={}, size={}",
                status, urgency, requesterId, title, page, size
        );
        return rfcApiService.getRfcs(page, size, status, urgency, requesterId, title);
    }

    @Override
    public RfcResponse getRfcById(Long id) {
        log.info("GET /api/rfc/{} - Getting RFC by ID", id);
        return rfcApiService.getRfcById(id);
    }

    @Override
    public RfcResponse updateRfc(Long id, RfcRequest rfcRequest) {
        log.info("PUT /api/rfc/{} - Updating RFC", id);
        return rfcApiService.updateRfc(id, rfcRequest);
    }

    @Override
    public void deleteRfc(Long id) {
        log.info("DELETE /api/rfc/{} - Deleting RFC", id);
        rfcApiService.deleteRfc(id);
    }

    @Override
    public AffectedSubsystemResponse updateSubsystemConfirmationStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemConfirmationStatusRequest request
    ) {
        log.info("PATCH /api/rfc/{}/subsystem/{}/confirmation - Updating confirmation status to {}",
                rfcId, subsystemId, request.getStatus());
        return subsystemStatusApiService.updateConfirmationStatus(rfcId, subsystemId, request);
    }

    @Override
    public AffectedSubsystemResponse updateSubsystemExecutionStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemExecutionStatusRequest request
    ) {
        log.info("PATCH /api/rfc/{}/subsystem/{}/execution - Updating execution status to {}",
                rfcId, subsystemId, request.getStatus());
        return subsystemStatusApiService.updateExecutionStatus(rfcId, subsystemId, request);
    }

    @Override
    public RfcApprovalResponse approveRfc(Long id, ApproveRfcRequest approveRfcRequest) {
        log.info("POST /api/rfc/{}/approve - Approving RFC", id);
        return rfcApprovalApiService.approveRfc(id, approveRfcRequest);
    }

    @Override
    public RfcApprovalResponse unapproveRfc(Long id, UnapproveRfcRequest unapproveRfcRequest) {
        log.info("POST /api/rfc/{}/unapprove - Unapproving RFC", id);
        return rfcApprovalApiService.unapproveRfc(id, unapproveRfcRequest);
    }

    @Override
    public RfcApprovalsResponse getRfcApprovals(Long id) {
        log.info("GET /api/rfc/{}/approvals - Getting RFC approvals", id);
        return rfcApprovalApiService.getRfcApprovals(id);
    }

    @Override
    public RfcHistoryResponse getRfcHistory(Long id, Integer page, Integer size) {
        log.info("GET /api/rfc/{}/history - Getting RFC history: page={}, size={}", id, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return rfcHistoryService.getRfcHistory(id, pageable);
    }
}

