package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.SubsystemRequest;
import ru.c21501.rfcservice.openapi.model.SubsystemResponse;

import java.util.List;

/**
 * API-сервис для работы с подсистемами (слой маппинга)
 */
public interface SubsystemApiService {

    /**
     * Создает новую подсистему
     *
     * @param systemId ID родительской системы
     * @param request  данные для создания подсистемы
     * @return созданная подсистема
     */
    SubsystemResponse createSubsystem(Long systemId, SubsystemRequest request);

    /**
     * Обновляет данные подсистемы
     *
     * @param id      ID подсистемы
     * @param request новые данные подсистемы
     * @return обновленная подсистема
     */
    SubsystemResponse updateSubsystem(Long id, SubsystemRequest request);

    /**
     * Получает подсистему по ID
     *
     * @param id ID подсистемы
     * @return данные подсистемы
     */
    SubsystemResponse getSubsystemById(Long id);

    /**
     * Удаляет подсистему
     *
     * @param id ID подсистемы
     */
    void deleteSubsystem(Long id);

    /**
     * Получает список подсистем системы
     *
     * @param systemId ID системы
     * @return список подсистем
     */
    List<SubsystemResponse> getSubsystemsBySystemId(Long systemId);
}