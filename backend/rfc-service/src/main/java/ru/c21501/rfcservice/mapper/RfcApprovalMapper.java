package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;
import ru.c21501.rfcservice.openapi.model.RfcApprovalResponse;

/**
 * Маппер для RfcApproval
 */
@Mapper(componentModel = "spring")
public interface RfcApprovalMapper {

    @Mapping(target = "rfcId", source = "rfc.id")
    @Mapping(target = "approverId", source = "approver.id")
    @Mapping(target = "approverName", source = "approver.username")
    RfcApprovalResponse toResponse(RfcApprovalEntity entity);
}