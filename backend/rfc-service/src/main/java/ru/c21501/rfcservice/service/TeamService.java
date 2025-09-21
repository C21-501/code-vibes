package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с командами
 */
public interface TeamService {

    /**
     * Создать новую команду
     */
    Team createTeam(Team team);

    /**
     * Обновить команду
     */
    Team updateTeam(Team team);

    /**
     * Найти команду по ID
     */
    Optional<Team> findById(UUID id);

    /**
     * Найти команду по названию
     */
    Optional<Team> findByName(String name);

    /**
     * Найти команды по руководителю
     */
    List<Team> findByLeader(User leader);

    /**
     * Найти команды по ID руководителя
     */
    List<Team> findByLeaderId(UUID leaderId);

    /**
     * Получить все команды
     */
    List<Team> findAll();

    /**
     * Проверить существование команды по ID
     */
    boolean existsById(UUID id);

    /**
     * Проверить существование команды по названию
     */
    boolean existsByName(String name);

    /**
     * Удалить команду по ID
     */
    void deleteById(UUID id);
}
