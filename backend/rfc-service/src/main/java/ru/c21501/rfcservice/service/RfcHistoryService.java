package ru.c21501.rfcservice.service;

import org.springframework.data.domain.Pageable;
import ru.c21501.rfcservice.openapi.model.RfcHistoryResponse;

/**
 * Сервис для работы с историей изменений RFC
 */
public interface RfcHistoryService {

    /**
     * Получить историю изменений RFC
     *
     * @param rfcId    ID RFC
     * @param pageable параметры пагинации
     * @return историю изменений с пагинацией
     */
    RfcHistoryResponse getRfcHistory(Long rfcId, Pageable pageable);
}
