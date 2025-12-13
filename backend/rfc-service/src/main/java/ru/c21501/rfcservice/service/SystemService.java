package ru.c21501.rfcservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.c21501.rfcservice.model.entity.SystemEntity;

/**
 * Сервис для работы с системами (бизнес-логика)
 */
public interface SystemService {

    /**
     * Создает новую систему
     *
     * @param systemEntity данные системы
     * @return созданная система
     */
    SystemEntity createSystem(SystemEntity systemEntity);

    /**
     * Обновляет данные системы
     *
     * @param id           ID системы
     * @param systemEntity новые данные системы
     * @return обновленная система
     */
    SystemEntity updateSystem(Long id, SystemEntity systemEntity);

    /**
     * Получает систему по ID
     *
     * @param id ID системы
     * @return система
     */
    SystemEntity getSystemById(Long id);

    /**
     * Удаляет систему
     *
     * @param id ID системы
     */
    void deleteSystem(Long id);

    /**
     * Получает пагинированный список систем с поиском
     *
     * @param searchString строка поиска (опционально)
     * @param pageable     параметры пагинации
     * @return страница систем
     */
    Page<SystemEntity> getSystems(String searchString, Pageable pageable);
}