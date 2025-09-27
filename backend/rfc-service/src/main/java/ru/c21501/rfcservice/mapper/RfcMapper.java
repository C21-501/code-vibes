package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.c21501.rfcservice.dto.request.CreateRfcRequest;
import ru.c21501.rfcservice.dto.request.UpdateRfcRequest;
import ru.c21501.rfcservice.dto.response.RfcResponse;
import ru.c21501.rfcservice.model.entity.Rfc;

/**
 * MapStruct маппер для RFC
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, SystemMapper.class})
public interface RfcMapper {

    /**
     * Преобразование RFC entity в DTO ответа
     */
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "requester", target = "requester")
    @Mapping(source = "system", target = "system")
    RfcResponse toResponse(Rfc rfc);

    /**
     * Преобразование CreateRfcRequest в RFC entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "reviewers", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "implementedAt", ignore = true)
    @Mapping(target = "actualStartDate", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "actualDuration", ignore = true)
    Rfc toEntity(CreateRfcRequest request);

    /**
     * Обновление RFC entity из UpdateRfcRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "system", ignore = true)
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "reviewers", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "submittedAt", ignore = true)
    @Mapping(target = "approvedAt", ignore = true)
    @Mapping(target = "implementedAt", ignore = true)
    @Mapping(target = "actualStartDate", ignore = true)
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "actualDuration", ignore = true)
    void updateEntityFromRequest(UpdateRfcRequest request, @MappingTarget Rfc rfc);
}
