package ru.c21501.rfcservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.SystemEntity;

/**
 * Репозиторий для работы с системами
 */
@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, Long> {

    /**
     * Проверка существования системы по названию
     *
     * @param name название системы
     * @return true, если система существует
     */
    boolean existsByName(String name);

    /**
     * Поиск систем по строке поиска с пагинацией
     * Поиск осуществляется по ID (если строка - число), name, description
     *
     * @param searchString строка поиска
     * @param pageable     параметры пагинации
     * @return страница систем
     */
    @Query("SELECT s FROM SystemEntity s WHERE " +
            "CAST(s.id AS string) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchString, '%')) OR " +
            "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchString, '%'))")
    Page<SystemEntity> searchSystems(@Param("searchString") String searchString, Pageable pageable);
}
