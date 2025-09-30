package ru.c21501.rfcservice.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO для запроса загрузки вложения
 */
@Data
public class UploadAttachmentRequest {
    private String description;
}