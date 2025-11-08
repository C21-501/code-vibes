package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.RfcApprovalEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;

import java.util.List;

/**
 * Сервис для работы с согласованиями RFC
 */
public interface RfcApprovalService {

    /**
     * Согласовать RFC
     *
     * @param rfcId ID RFC
     * @param comment комментарий
     * @param currentUser текущий пользователь
     * @return запись о согласовании
     */
    RfcApprovalEntity approveRfc(Long rfcId, String comment, UserEntity currentUser);

    /**
     * Отменить согласование RFC
     *
     * @param rfcId ID RFC
     * @param comment комментарий
     * @param currentUser текущий пользователь
     * @return запись о согласовании
     */
    RfcApprovalEntity unapproveRfc(Long rfcId, String comment, UserEntity currentUser);

    /**
     * Получить все согласования для RFC
     *
     * @param rfcId ID RFC
     * @return список согласований
     */
    List<RfcApprovalEntity> getRfcApprovals(Long rfcId);
}