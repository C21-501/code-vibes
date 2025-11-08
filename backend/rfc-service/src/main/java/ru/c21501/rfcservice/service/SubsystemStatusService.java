package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.RfcAffectedSubsystemEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;

/**
 * Сервис для работы со статусами затронутых подсистем
 */
public interface SubsystemStatusService {

    /**
     * Обновляет статус подтверждения подсистемы
     *
     * @param rfcId ID RFC
     * @param subsystemId ID связи с подсистемой
     * @param newStatus новый статус
     * @param comment комментарий
     * @param currentUser текущий пользователь
     * @return обновленная сущность
     */
    RfcAffectedSubsystemEntity updateConfirmationStatus(
            Long rfcId,
            Long subsystemId,
            ConfirmationStatus newStatus,
            String comment,
            UserEntity currentUser
    );

    /**
     * Обновляет статус выполнения подсистемы
     *
     * @param rfcId ID RFC
     * @param subsystemId ID связи с подсистемой
     * @param newStatus новый статус
     * @param comment комментарий
     * @param currentUser текущий пользователь
     * @return обновленная сущность
     */
    RfcAffectedSubsystemEntity updateExecutionStatus(
            Long rfcId,
            Long subsystemId,
            ExecutionStatus newStatus,
            String comment,
            UserEntity currentUser
    );
}
