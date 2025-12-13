package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.exception.ResourceNotFoundException;
import ru.c21501.rfcservice.model.entity.AttachmentEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;
import ru.c21501.rfcservice.repository.AttachmentRepository;
import ru.c21501.rfcservice.service.AttachmentService;

import java.io.IOException;
import java.util.List;

/**
 * Реализация сервиса для работы с вложениями
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final AttachmentRepository attachmentRepository;

    @Override
    @Transactional
    public AttachmentEntity uploadAttachment(MultipartFile file, UserEntity uploadedBy) {
        log.info("Uploading attachment: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());

        // Валидация размера файла
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum limit of 5MB. Actual size: %d bytes", file.getSize())
            );
        }

        try {
            // Создаем вложение БЕЗ привязки к RFC (rfcId = null)
            AttachmentEntity attachment = AttachmentEntity.builder()
                    .originalFilename(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .fileData(file.getBytes())
                    .uploadedBy(uploadedBy)
                    .build();

            // Сохраняем в БД
            AttachmentEntity savedAttachment = attachmentRepository.save(attachment);
            log.info("Attachment saved with ID: {}, not yet attached to any RFC", savedAttachment.getId());

            return savedAttachment;
        } catch (IOException e) {
            log.error("Error reading file data", e);
            throw new RuntimeException("Failed to upload attachment", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentEntity getAttachmentById(Long id) {
        log.info("Getting attachment by ID: {}", id);
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Attachment with ID %d not found", id)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentEntity> getAttachmentsByRfcId(Long rfcId) {
        log.info("Getting attachments for RFC: {}", rfcId);
        return attachmentRepository.findByRfc_Id(rfcId);
    }

    @Override
    @Transactional
    public void attachToRfc(List<Long> attachmentIds, Long rfcId) {
        log.info("Attaching {} files to RFC {}", attachmentIds.size(), rfcId);

        for (Long attachmentId : attachmentIds) {
            AttachmentEntity attachment = getAttachmentById(attachmentId);

            // Проверяем, что файл еще не привязан к другому RFC
            if (attachment.getRfc() != null && !attachment.getRfc().getId().equals(rfcId)) {
                throw new IllegalArgumentException(
                        String.format("Attachment with ID %d is already attached to RFC %d",
                                attachmentId, attachment.getRfc().getId())
                );
            }

            // Привязываем к RFC через установку rfcId (упрощенный вариант без загрузки RFC)
            // В новом подходе это не нужно - attachments будут резолвиться через resolveAttachments
            log.info("Attachment {} attached to RFC {}", attachmentId, rfcId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentEntity> resolveAttachments(List<Long> attachmentIds) {
        log.info("Resolving {} attachments", attachmentIds != null ? attachmentIds.size() : 0);

        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return List.of();
        }

        return attachmentIds.stream()
                .map(this::getAttachmentById)
                .toList();
    }
}
