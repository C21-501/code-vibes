package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.ApproveRfcRequest;
import ru.c21501.rfcservice.openapi.model.RfcApprovalResponse;
import ru.c21501.rfcservice.openapi.model.RfcApprovalsResponse;
import ru.c21501.rfcservice.openapi.model.UnapproveRfcRequest;

/**
 * API-сервис для работы с согласованиями RFC
 */
public interface RfcApprovalApiService {

    /**
     * Согласовать RFC
     *
     * @param rfcId ID RFC
     * @param request запрос с комментарием (опционально)
     * @return информация о согласовании
     */
    RfcApprovalResponse approveRfc(Long rfcId, ApproveRfcRequest request);

    /**
     * Отменить согласование RFC
     *
     * @param rfcId ID RFC
     * @param request запрос с комментарием (опционально)
     * @return информация о согласовании
     */
    RfcApprovalResponse unapproveRfc(Long rfcId, UnapproveRfcRequest request);

    /**
     * Получить список всех согласований для RFC
     *
     * @param rfcId ID RFC
     * @return список согласований
     */
    RfcApprovalsResponse getRfcApprovals(Long rfcId);
}
