package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.SubsystemEntity;

import java.util.List;

/**
 * Репозиторий для работы с подсистемами
 */
@Repository
public interface SubsystemRepository extends JpaRepository<SubsystemEntity, Long> {

    /**
     * Получить все подсистемы системы
     *
     * @param systemId ID системы
     * @return список подсистем
     */
    List<SubsystemEntity> findBySystemId(Long systemId);

    /**
     * Проверка существования подсистемы по названию в рамках системы
     *
     * @param name     название подсистемы
     * @param systemId ID системы
     * @return true, если подсистема существует
     */
    boolean existsByNameAndSystemId(String name, Long systemId);
}
