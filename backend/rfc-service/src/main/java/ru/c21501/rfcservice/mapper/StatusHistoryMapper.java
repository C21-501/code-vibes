package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import ru.c21501.rfcservice.dto.response.StatusHistoryResponse;
import ru.c21501.rfcservice.model.entity.StatusHistory;

/**
 * MapStruct маппер для StatusHistory
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface StatusHistoryMapper {

    /**
     * Преобразование StatusHistory entity в DTO ответа
     */
    StatusHistoryResponse toResponse(StatusHistory statusHistory);
}
