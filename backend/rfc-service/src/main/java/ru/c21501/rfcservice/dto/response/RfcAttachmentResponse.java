package ru.c21501.rfcservice.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для ответа с данными вложения
 */
@Data
public class RfcAttachmentResponse {
    private UUID id;
    private String fileName;
    private String originalFileName;
    private Long fileSize;
    private String mimeType;
    private String description;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}