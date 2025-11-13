package ru.c21501.rfcservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.RfcHistoryEntity;

import java.util.List;

/**
 * Repository для работы с историей изменений RFC
 */
@Repository
public interface RfcHistoryRepository extends JpaRepository<RfcHistoryEntity, Long> {

    /**
     * Найти всю историю изменений RFC с пагинацией
     *
     * @param rfcId    ID RFC
     * @param pageable параметры пагинации и сортировки
     * @return страница истории изменений
     */
    Page<RfcHistoryEntity> findByRfcIdOrderByCreateDatetimeDesc(Long rfcId, Pageable pageable);

    /**
     * Найти всю историю изменений RFC (для вычисления diff)
     *
     * @param rfcId ID RFC
     * @return список всей истории изменений, отсортированный по времени создания
     */
    @Query("SELECT h FROM RfcHistoryEntity h " +
            "LEFT JOIN FETCH h.changedBy " +
            "LEFT JOIN FETCH h.requester " +
            "WHERE h.rfcId = :rfcId " +
            "ORDER BY h.createDatetime DESC")
    List<RfcHistoryEntity> findAllByRfcIdWithUsers(@Param("rfcId") Long rfcId);
}