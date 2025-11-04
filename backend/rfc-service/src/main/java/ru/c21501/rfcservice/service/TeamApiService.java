package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.TeamPageResponse;
import ru.c21501.rfcservice.openapi.model.TeamRequest;
import ru.c21501.rfcservice.openapi.model.TeamResponse;

/**
 * API-сервис для работы с командами (слой маппинга)
 */
public interface TeamApiService {

    /**
     * Создает новую команду
     *
     * @param request данные для создания команды
     * @return созданная команда
     */
    TeamResponse createTeam(TeamRequest request);

    /**
     * Обновляет данные команды
     *
     * @param id      ID команды
     * @param request новые данные команды
     * @return обновленная команда
     */
    TeamResponse updateTeam(Long id, TeamRequest request);

    /**
     * Получает команду по ID
     *
     * @param id ID команды
     * @return данные команды
     */
    TeamResponse getTeamById(Long id);

    /**
     * Удаляет команду
     *
     * @param id ID команды
     */
    void deleteTeam(Long id);

    /**
     * Получает пагинированный список команд с поиском
     *
     * @param page         номер страницы
     * @param size         размер страницы
     * @param searchString строка поиска (опционально)
     * @return страница команд
     */
    TeamPageResponse getTeams(Integer page, Integer size, String searchString);
}
