package ru.c21501.rfcservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.TeamEntity;

/**
 * Репозиторий для работы с командами
 */
@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    /**
     * Проверка существования команды по названию
     *
     * @param name название команды
     * @return true, если команда существует
     */
    boolean existsByName(String name);

    /**
     * Поиск команд по строке поиска с пагинацией
     * Поиск осуществляется по ID (если строка - число), name, description
     *
     * @param searchString строка поиска
     * @param pageable     параметры пагинации
     * @return страница команд
     */
    @Query("SELECT t FROM TeamEntity t WHERE " +
            "CAST(t.id AS string) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    Page<TeamEntity> searchTeams(@Param("searchString") String searchString, Pageable pageable);
}
