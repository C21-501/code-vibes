package ru.c21501.rfcservice.mapper;

import org.mapstruct.Mapper;
import ru.c21501.rfcservice.dto.response.RfcAttachmentResponse;
import ru.c21501.rfcservice.model.entity.RfcAttachment;

/**
 * MapStruct маппер для RfcAttachment
 */
@Mapper(componentModel = "spring")
public interface RfcAttachmentMapper {

    /**
     * Преобразование RfcAttachment entity в DTO ответа
     */
    RfcAttachmentResponse toResponse(RfcAttachment attachment);
}
