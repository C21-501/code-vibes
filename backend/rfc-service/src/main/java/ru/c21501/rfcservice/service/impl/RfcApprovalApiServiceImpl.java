package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.c21501.rfcservice.mapper.RfcApprovalMapper;
import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.ApproveRfcRequest;
import ru.c21501.rfcservice.openapi.model.RfcApprovalResponse;
import ru.c21501.rfcservice.openapi.model.RfcApprovalsResponse;
import ru.c21501.rfcservice.openapi.model.UnapproveRfcRequest;
import ru.c21501.rfcservice.service.RfcApprovalApiService;
import ru.c21501.rfcservice.service.RfcApprovalService;
import ru.c21501.rfcservice.service.SecurityContextService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация API-сервиса для работы с согласованиями RFC
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RfcApprovalApiServiceImpl implements RfcApprovalApiService {

    private final RfcApprovalService rfcApprovalService;
    private final RfcApprovalMapper rfcApprovalMapper;
    private final SecurityContextService securityContextService;

    @Override
    public RfcApprovalResponse approveRfc(Long rfcId, ApproveRfcRequest request) {
        log.info("Approving RFC {}", rfcId);

        UserEntity currentUser = securityContextService.getCurrentUser();
        String comment = request != null ? request.getComment() : null;

        RfcApprovalEntity approval = rfcApprovalService.approveRfc(rfcId, comment, currentUser);

        return rfcApprovalMapper.toResponse(approval);
    }

    @Override
    public RfcApprovalResponse unapproveRfc(Long rfcId, UnapproveRfcRequest request) {
        log.info("Unapproving RFC {}", rfcId);

        UserEntity currentUser = securityContextService.getCurrentUser();
        String comment = request != null ? request.getComment() : null;

        RfcApprovalEntity approval = rfcApprovalService.unapproveRfc(rfcId, comment, currentUser);

        return rfcApprovalMapper.toResponse(approval);
    }

    @Override
    public RfcApprovalsResponse getRfcApprovals(Long rfcId) {
        log.info("Getting approvals for RFC {}", rfcId);

        List<RfcApprovalEntity> approvals = rfcApprovalService.getRfcApprovals(rfcId);

        List<RfcApprovalResponse> approvalResponses = approvals.stream()
                .map(rfcApprovalMapper::toResponse)
                .collect(Collectors.toList());

        RfcApprovalsResponse response = new RfcApprovalsResponse();
        response.setRfcId(rfcId);
        response.setApprovals(approvalResponses);

        return response;
    }
}