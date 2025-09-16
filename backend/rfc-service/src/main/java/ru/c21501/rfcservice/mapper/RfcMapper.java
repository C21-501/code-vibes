package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.c21501.rfcservice.dto.request.CreateRfcRequest;
import ru.c21501.rfcservice.dto.request.UpdateRfcRequest;
import ru.c21501.rfcservice.dto.response.RfcResponse;
import ru.c21501.rfcservice.model.entity.Rfc;

/**
 * MapStruct маппер для RFC
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface RfcMapper {
    
    /**
     * Преобразование RFC entity в DTO ответа
     */
    RfcResponse toResponse(Rfc rfc);
    
    /**
     * Преобразование CreateRfcRequest в RFC entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "reviewers", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    Rfc toEntity(CreateRfcRequest request);
    
    /**
     * Обновление RFC entity из UpdateRfcRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "executors", ignore = true)
    @Mapping(target = "reviewers", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    void updateEntityFromRequest(UpdateRfcRequest request, @MappingTarget Rfc rfc);
}
