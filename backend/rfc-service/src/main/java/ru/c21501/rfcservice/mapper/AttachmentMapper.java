package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.c21501.rfcservice.model.entity.AttachmentEntity;
import ru.c21501.rfcservice.openapi.model.AttachmentResponse;

/**
 * Mapper для преобразования между AttachmentEntity и DTO
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AttachmentMapper {

    /**
     * Преобразует AttachmentEntity в AttachmentResponse
     *
     * @param entity сущность вложения
     * @return ответ с данными вложения
     */
    @Mapping(target = "uploadedById", source = "uploadedBy.id")
    @Mapping(target = "uploadedByName", source = "uploadedBy.username")
    AttachmentResponse toResponse(AttachmentEntity entity);
}