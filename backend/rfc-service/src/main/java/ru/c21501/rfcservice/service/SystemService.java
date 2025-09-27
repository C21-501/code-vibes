package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.System;
import ru.c21501.rfcservice.model.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с подсистемами
 */
public interface SystemService {

    /**
     * Создать новую подсистему
     */
    System createSystem(System system);

    /**
     * Обновить подсистему
     */
    System updateSystem(System system);

    /**
     * Найти подсистему по ID
     */
    Optional<System> findById(UUID id);

    /**
     * Найти подсистему по названию
     */
    Optional<System> findByName(String name);

    /**
     * Найти подсистемы по типу
     */
    List<System> findByType(String type);

    /**
     * Найти подсистемы по ответственной команде
     */
    List<System> findByResponsibleTeam(Team responsibleTeam);

    /**
     * Найти подсистемы по ID ответственной команды
     */
    List<System> findByResponsibleTeamId(UUID responsibleTeamId);

    /**
     * Получить все подсистемы
     */
    List<System> findAll();

    /**
     * Проверить существование подсистемы по ID
     */
    boolean existsById(UUID id);

    /**
     * Проверить существование подсистемы по названию
     */
    boolean existsByName(String name);

    /**
     * Удалить подсистему по ID
     */
    void deleteById(UUID id);
}
