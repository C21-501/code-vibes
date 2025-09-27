package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.System;
import ru.c21501.rfcservice.model.entity.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с подсистемами
 */
@Repository
public interface SystemRepository extends JpaRepository<System, UUID> {

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
     * Проверить существование подсистемы по названию
     */
    boolean existsByName(String name);
}
