package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.AffectedSubsystemResponse;
import ru.c21501.rfcservice.openapi.model.UpdateSubsystemConfirmationStatusRequest;
import ru.c21501.rfcservice.openapi.model.UpdateSubsystemExecutionStatusRequest;

/**
 * API-сервис для работы со статусами затронутых подсистем
 */
public interface SubsystemStatusApiService {

    /**
     * Обновить статус подтверждения подсистемы
     *
     * @param rfcId ID RFC
     * @param subsystemId ID связи с подсистемой
     * @param request запрос с новым статусом
     * @return обновленная подсистема
     */
    AffectedSubsystemResponse updateConfirmationStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemConfirmationStatusRequest request
    );

    /**
     * Обновить статус выполнения подсистемы
     *
     * @param rfcId ID RFC
     * @param subsystemId ID связи с подсистемой
     * @param request запрос с новым статусом
     * @return обновленная подсистема
     */
    AffectedSubsystemResponse updateExecutionStatus(
            Long rfcId,
            Long subsystemId,
            UpdateSubsystemExecutionStatusRequest request
    );
}
