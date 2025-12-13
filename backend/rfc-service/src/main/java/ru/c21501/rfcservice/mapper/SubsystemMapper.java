package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.c21501.rfcservice.model.entity.SubsystemEntity;
import ru.c21501.rfcservice.openapi.model.SubsystemRequest;
import ru.c21501.rfcservice.openapi.model.SubsystemResponse;

/**
 * Mapper для преобразования между SubsystemEntity и DTO
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SubsystemMapper {

    /**
     * Преобразует SubsystemRequest в SubsystemEntity
     *
     * @param request запрос с данными подсистемы
     * @return сущность подсистемы
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "createDatetime", ignore = true)
    @Mapping(target = "updateDatetime", ignore = true)
    SubsystemEntity toEntity(SubsystemRequest request);

    /**
     * Преобразует SubsystemEntity в SubsystemResponse
     *
     * @param entity сущность подсистемы
     * @return ответ с данными подсистемы
     */
    @Mapping(target = "systemId", source = "system.id")
    @Mapping(target = "teamId", source = "team.id")
    SubsystemResponse toResponse(SubsystemEntity entity);
}