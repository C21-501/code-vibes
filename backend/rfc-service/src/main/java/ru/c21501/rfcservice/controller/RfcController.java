package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.c21501.rfcservice.mapper.RfcApprovalMapper;
import ru.c21501.rfcservice.mapper.RfcMapper;
import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.api.RfcApi;
import ru.c21501.rfcservice.openapi.model.*;
import ru.c21501.rfcservice.resolver.RfcActionResolver;
import ru.c21501.rfcservice.service.RfcApiService;
import ru.c21501.rfcservice.service.RfcApprovalService;
import ru.c21501.rfcservice.service.SecurityContextService;
import ru.c21501.rfcservice.service.SubsystemStatusService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с RFC
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RfcController implements RfcApi {

    private final RfcApiService rfcApiService;
    private final SubsystemStatusService subsystemStatusService;
    private final RfcApprovalService rfcApprovalService;
    private final SecurityContextService securityContextService;
    private final RfcMapper rfcMapper;
    private final RfcApprovalMapper rfcApprovalMapper;
    private final RfcActionResolver actionResolver;

    @Override
    public RfcResponse createRfc(RfcRequest rfcRequest) {
        log.info("POST /api/rfc - Creating RFC: {}", rfcRequest.getTitle());
        return rfcApiService.createRfc(rfcRequest);
    }

    @Override
    public RfcPageResponse getRfcs(Integer page, Integer size, String status, String urgency, Long requesterId) {
        log.info("GET /api/rfc - Getting RFC list with filters: status={}, urgency={}, requesterId={}, page={}, size={}",
                status, urgency, requesterId, page, size);
        return rfcApiService.getRfcs(page, size, status, urgency, requesterId);
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
    public AffectedSubsystemResponse updateSubsystemExecutionStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemExecutionStatusRequest request
    ) {
        log.info("PATCH /api/rfc/{}/subsystem/{}/execution - Updating execution status to {}",
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

    @Override
    public RfcApprovalResponse approveRfc(Long id, ApproveRfcRequest approveRfcRequest) {
        log.info("POST /api/rfc/{}/approve - Approving RFC", id);

        UserEntity currentUser = securityContextService.getCurrentUser();
        String comment = approveRfcRequest != null ? approveRfcRequest.getComment() : null;

        RfcApprovalEntity approval = rfcApprovalService.approveRfc(id, comment, currentUser);

        return rfcApprovalMapper.toResponse(approval);
    }

    @Override
    public RfcApprovalResponse unapproveRfc(Long id, UnapproveRfcRequest unapproveRfcRequest) {
        log.info("POST /api/rfc/{}/unapprove - Unapproving RFC", id);

        UserEntity currentUser = securityContextService.getCurrentUser();
        String comment = unapproveRfcRequest != null ? unapproveRfcRequest.getComment() : null;

        RfcApprovalEntity approval = rfcApprovalService.unapproveRfc(id, comment, currentUser);

        return rfcApprovalMapper.toResponse(approval);
    }

    @Override
    public RfcApprovalsResponse getRfcApprovals(Long id) {
        log.info("GET /api/rfc/{}/approvals - Getting RFC approvals", id);

        List<RfcApprovalEntity> approvals = rfcApprovalService.getRfcApprovals(id);

        List<RfcApprovalResponse> approvalResponses = approvals.stream()
                .map(rfcApprovalMapper::toResponse)
                .collect(Collectors.toList());

        RfcApprovalsResponse response = new RfcApprovalsResponse();
        response.setRfcId(id);
        response.setApprovals(approvalResponses);

        return response;
    }
}
