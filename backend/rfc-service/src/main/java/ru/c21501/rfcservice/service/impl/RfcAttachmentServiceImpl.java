package ru.c21501.rfcservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcAttachment;
import ru.c21501.rfcservice.repository.RfcAttachmentRepository;
import ru.c21501.rfcservice.service.RfcAttachmentService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RfcAttachmentServiceImpl implements RfcAttachmentService {

    private final RfcAttachmentRepository attachmentRepository;

    @Value("${app.file.storage-path:./uploads}")
    private String storagePath;

    @Override
    public RfcAttachment uploadAttachment(Rfc rfc, MultipartFile file, String description, String uploadedBy) {
        try {
            // Создаем директорию если не существует
            Path uploadPath = Paths.get(storagePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Генерируем уникальное имя файла
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String storedFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(storedFileName);

            // Сохраняем файл
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Создаем запись в БД
            RfcAttachment attachment = RfcAttachment.builder()
                    .rfc(rfc)
                    .fileName(storedFileName)
                    .originalFileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType())
                    .filePath(filePath.toString())
                    .description(description)
                    .uploadedBy(uploadedBy)
                    .build();

            RfcAttachment savedAttachment = attachmentRepository.save(attachment);
            log.info("File uploaded successfully: {} for RFC {}", file.getOriginalFilename(), rfc.getId());
            return savedAttachment;

        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    @Override
    public Optional<RfcAttachment> findById(UUID id) {
        return attachmentRepository.findById(id);
    }

    @Override
    public List<RfcAttachment> findByRfc(Rfc rfc) {
        return attachmentRepository.findByRfc(rfc);
    }

    @Override
    public List<RfcAttachment> findByRfcId(UUID rfcId) {
        return attachmentRepository.findByRfcId(rfcId);
    }

    @Override
    public Resource downloadAttachment(UUID attachmentId) {
        try {
            RfcAttachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));

            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error while reading file", e);
        }
    }

    @Override
    public void deleteAttachment(UUID attachmentId) {
        try {
            RfcAttachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new RuntimeException("Attachment not found"));

            // Удаляем физический файл
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);

            // Удаляем запись из БД
            attachmentRepository.delete(attachment);
            log.info("Attachment deleted: {}", attachmentId);

        } catch (IOException e) {
            log.error("Failed to delete attachment file: {}", attachmentId, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllByRfcId(UUID rfcId) {
        List<RfcAttachment> attachments = attachmentRepository.findByRfcId(rfcId);
        for (RfcAttachment attachment : attachments) {
            deleteAttachment(attachment.getId());
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return attachmentRepository.existsById(id);
    }

    @Override
    public Long getTotalSizeByRfcId(UUID rfcId) {
        return attachmentRepository.findByRfcId(rfcId).stream()
                .mapToLong(RfcAttachment::getFileSize)
                .sum();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}