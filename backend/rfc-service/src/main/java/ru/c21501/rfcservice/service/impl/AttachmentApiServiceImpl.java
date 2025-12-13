package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.mapper.AttachmentMapper;
import ru.c21501.rfcservice.model.entity.AttachmentEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.openapi.model.AttachmentResponse;
import ru.c21501.rfcservice.service.AttachmentApiService;
import ru.c21501.rfcservice.service.AttachmentService;
import ru.c21501.rfcservice.service.SecurityContextService;

/**
 * Реализация API-сервиса для работы с вложениями
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentApiServiceImpl implements AttachmentApiService {

    private final AttachmentService attachmentService;
    private final SecurityContextService securityContextService;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentResponse uploadAttachment(MultipartFile file) {
        log.info("Uploading attachment: {}", file.getOriginalFilename());

        // Получаем текущего пользователя из контекста безопасности
        UserEntity currentUser = securityContextService.getCurrentUser();

        // Загружаем файл
        AttachmentEntity savedAttachment = attachmentService.uploadAttachment(file, currentUser);

        // Преобразуем в DTO
        return attachmentMapper.toResponse(savedAttachment);
    }

    @Override
    public Resource downloadAttachment(Long id) {
        log.info("Downloading attachment with ID: {}", id);

        // Получаем файл из базы данных
        AttachmentEntity attachment = attachmentService.getAttachmentById(id);

        // Возвращаем файл как Resource
        return new ByteArrayResource(attachment.getFileData());
    }
}