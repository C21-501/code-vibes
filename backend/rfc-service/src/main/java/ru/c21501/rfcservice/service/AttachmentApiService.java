package ru.c21501.rfcservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.openapi.model.AttachmentResponse;

/**
 * API-сервис для работы с вложениями (преобразование в DTOs)
 */
public interface AttachmentApiService {

    /**
     * Загружает файл
     *
     * @param file файл
     * @return DTO с данными загруженного файла
     */
    AttachmentResponse uploadAttachment(MultipartFile file);

    /**
     * Скачивает файл по ID
     *
     * @param id ID файла
     * @return файл как Resource
     */
    Resource downloadAttachment(Long id);
}