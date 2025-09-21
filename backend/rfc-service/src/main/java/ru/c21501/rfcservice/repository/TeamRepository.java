package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Team;
import ru.c21501.rfcservice.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с командами
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

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
     * Проверить существование команды по названию
     */
    boolean existsByName(String name);
}
