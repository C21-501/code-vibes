package ru.c21501.rfcservice.service;

import ru.c21501.rfcservice.openapi.model.RfcRequest;
import ru.c21501.rfcservice.openapi.model.RfcResponse;

/**
 * API-сервис для работы с RFC
 */
public interface RfcApiService {

    /**
     * Создать новый RFC
     *
     * @param request данные для создания
     * @return созданный RFC
     */
    RfcResponse createRfc(RfcRequest request);

    /**
     * Обновить RFC
     *
     * @param id ID RFC
     * @param request данные для обновления
     * @return обновленный RFC
     */
    RfcResponse updateRfc(Long id, RfcRequest request);

    /**
     * Получить RFC по ID
     *
     * @param id ID RFC
     * @return найденный RFC
     */
    RfcResponse getRfcById(Long id);

    /**
     * Удалить RFC
     *
     * @param id ID RFC
     */
    void deleteRfc(Long id);

    /**
     * Получить список RFC с фильтрацией и пагинацией
     *
     * @param page        номер страницы
     * @param size        размер страницы
     * @param status      фильтр по статусу (опционально)
     * @param urgency     фильтр по срочности (опционально)
     * @param requesterId фильтр по ID создателя (опционально)
     * @return страница с RFC
     */
    ru.c21501.rfcservice.openapi.model.RfcPageResponse getRfcs(Integer page, Integer size, String status, String urgency, Long requesterId);
}