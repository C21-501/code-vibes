package ru.c21501.rfcservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.c21501.rfcservice.model.entity.TeamEntity;

import java.util.Set;

/**
 * Сервис для работы с командами (бизнес-логика)
 */
public interface TeamService {

    /**
     * Создает новую команду
     *
     * @param teamEntity данные команды
     * @param memberIds  список ID членов команды
     * @return созданная команда
     */
    TeamEntity createTeam(TeamEntity teamEntity, Set<Long> memberIds);

    /**
     * Обновляет данные команды
     *
     * @param id         ID команды
     * @param teamEntity новые данные команды
     * @param memberIds  список ID членов команды
     * @return обновленная команда
     */
    TeamEntity updateTeam(Long id, TeamEntity teamEntity, Set<Long> memberIds);

    /**
     * Получает команду по ID
     *
     * @param id ID команды
     * @return команда
     */
    TeamEntity getTeamById(Long id);

    /**
     * Удаляет команду
     *
     * @param id ID команды
     */
    void deleteTeam(Long id);

    /**
     * Получает пагинированный список команд с поиском
     *
     * @param searchString строка поиска (опционально)
     * @param pageable     параметры пагинации
     * @return страница команд
     */
    Page<TeamEntity> getTeams(String searchString, Pageable pageable);
}