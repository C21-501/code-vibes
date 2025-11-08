package ru.c21501.rfcservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.c21501.rfcservice.model.entity.RfcEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.RfcRequest;

/**
 * Сервис для работы с RFC
 */
public interface RfcService {

    /**
     * Создать новый RFC
     *
     * @param request данные для создания
     * @param requester пользователь-создатель
     * @return созданный RFC
     */
    RfcEntity createRfc(RfcRequest request, UserEntity requester);

    /**
     * Обновить RFC
     *
     * @param id ID RFC
     * @param request данные для обновления
     * @param updatedBy пользователь, обновивший RFC
     * @return обновленный RFC
     */
    RfcEntity updateRfc(Long id, RfcRequest request, UserEntity updatedBy);

    /**
     * Получить RFC по ID
     *
     * @param id ID RFC
     * @return найденный RFC
     */
    RfcEntity getRfcById(Long id);

    /**
     * Получить список RFC с фильтрацией и пагинацией
     *
     * @param status      фильтр по статусу (опционально)
     * @param urgency     фильтр по срочности (опционально)
     * @param requesterId фильтр по ID создателя (опционально)
     * @param pageable    параметры пагинации
     * @return страница с RFC
     */
    Page<RfcEntity> getRfcs(String status, String urgency, Long requesterId, Pageable pageable);

    /**
     * Удалить RFC (soft-delete)
     *
     * @param id ID RFC
     */
    void deleteRfc(Long id);
}