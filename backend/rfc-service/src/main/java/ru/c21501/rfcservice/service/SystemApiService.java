package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.SystemPageResponse;
import ru.c21501.rfcservice.openapi.model.SystemRequest;
import ru.c21501.rfcservice.openapi.model.SystemResponse;

/**
 * API-сервис для работы с системами (слой маппинга)
 */
public interface SystemApiService {

    /**
     * Создает новую систему
     *
     * @param request данные для создания системы
     * @return созданная система
     */
    SystemResponse createSystem(SystemRequest request);

    /**
     * Обновляет данные системы
     *
     * @param id      ID системы
     * @param request новые данные системы
     * @return обновленная система
     */
    SystemResponse updateSystem(Long id, SystemRequest request);

    /**
     * Получает систему по ID
     *
     * @param id ID системы
     * @return данные системы
     */
    SystemResponse getSystemById(Long id);

    /**
     * Удаляет систему
     *
     * @param id ID системы
     */
    void deleteSystem(Long id);

    /**
     * Получает пагинированный список систем с поиском
     *
     * @param page         номер страницы
     * @param size         размер страницы
     * @param searchString строка поиска (опционально)
     * @return страница систем
     */
    SystemPageResponse getSystems(Integer page, Integer size, String searchString);
}