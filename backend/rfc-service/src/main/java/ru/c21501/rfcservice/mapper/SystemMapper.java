package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.c21501.rfcservice.model.entity.SystemEntity;
import ru.c21501.rfcservice.openapi.model.SystemRequest;
import ru.c21501.rfcservice.openapi.model.SystemResponse;

/**
 * Mapper для преобразования между SystemEntity и DTO
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = SubsystemMapper.class
)
public interface SystemMapper {

    /**
     * Преобразует SystemRequest в SystemEntity
     *
     * @param request запрос с данными системы
     * @return сущность системы
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subsystems", ignore = true)
    @Mapping(target = "createDatetime", ignore = true)
    @Mapping(target = "updateDatetime", ignore = true)
    SystemEntity toEntity(SystemRequest request);

    /**
     * Преобразует SystemEntity в SystemResponse
     *
     * @param entity сущность системы
     * @return ответ с данными системы
     */
    SystemResponse toResponse(SystemEntity entity);
}