package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.SubsystemEntity;

import java.util.List;

/**
 * Сервис для работы с подсистемами (бизнес-логика)
 */
public interface SubsystemService {

    /**
     * Создает новую подсистему
     *
     * @param subsystemEntity данные подсистемы
     * @param systemId        ID родительской системы
     * @param teamId          ID ответственной команды
     * @return созданная подсистема
     */
    SubsystemEntity createSubsystem(SubsystemEntity subsystemEntity, Long systemId, Long teamId);

    /**
     * Обновляет данные подсистемы
     *
     * @param id              ID подсистемы
     * @param subsystemEntity новые данные подсистемы
     * @param systemId        ID родительской системы
     * @param teamId          ID ответственной команды
     * @return обновленная подсистема
     */
    SubsystemEntity updateSubsystem(Long id, SubsystemEntity subsystemEntity, Long systemId, Long teamId);

    /**
     * Получает подсистему по ID
     *
     * @param id ID подсистемы
     * @return подсистема
     */
    SubsystemEntity getSubsystemById(Long id);

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
    List<SubsystemEntity> getSubsystemsBySystemId(Long systemId);
}