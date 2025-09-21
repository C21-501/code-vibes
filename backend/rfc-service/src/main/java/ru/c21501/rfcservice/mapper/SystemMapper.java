package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import ru.c21501.rfcservice.dto.response.SystemResponse;
import ru.c21501.rfcservice.model.entity.System;

/**
 * MapStruct маппер для System
 */
@Mapper(componentModel = "spring", uses = {TeamMapper.class})
public interface SystemMapper {

    /**
     * Преобразование System entity в DTO ответа
     */
    SystemResponse toResponse(System system);
}
