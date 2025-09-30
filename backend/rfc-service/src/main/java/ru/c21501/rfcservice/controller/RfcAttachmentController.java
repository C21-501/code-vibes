package ru.c21501.rfcservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.dto.request.UploadAttachmentRequest;
import ru.c21501.rfcservice.dto.response.RfcAttachmentResponse;
import ru.c21501.rfcservice.mapper.RfcAttachmentMapper;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.User;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.service.RfcAttachmentService;
import ru.c21501.rfcservice.service.RfcService;
import ru.c21501.rfcservice.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * REST контроллер для работы с вложениями RFC
 */
@RestController
@RequestMapping("/api/rfcs/{rfcId}/attachments")
@RequiredArgsConstructor
public class RfcAttachmentController {

    private static final Logger log = LoggerFactory.getLogger(RfcAttachmentController.class);

    private final RfcAttachmentService attachmentService;
    private final RfcService rfcService;
    private final UserService userService;
    private final RfcAttachmentMapper attachmentMapper;

    /**
     * Получить список вложений RFC
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RfcAttachmentResponse>> getAttachments(@PathVariable UUID rfcId) {
        log.info("Getting attachments for RFC: {}", rfcId);

        if (!rfcService.existsById(rfcId)) {
            return ResponseEntity.notFound().build();
        }

        List<RfcAttachmentResponse> attachments = attachmentService.findByRfcId(rfcId).stream()
                .map(attachmentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(attachments);
    }

    /**
     * Загрузить новое вложение
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN') or @rfcSecurityService.isRequester(#rfcId, authentication)")
    public ResponseEntity<RfcAttachmentResponse> uploadAttachment(
            @PathVariable UUID rfcId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "request", required = false) @Valid UploadAttachmentRequest request,
            Authentication authentication) {

        log.info("Uploading attachment for RFC: {}, file: {}", rfcId, file.getOriginalFilename());

        try {
            // Получить RFC
            Rfc rfc = rfcService.findById(rfcId)
                    .orElseThrow(() -> new RuntimeException("RFC not found"));

            // Получить текущего пользователя
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String keycloakId = jwt.getSubject();
            User currentUser = userService.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

            // Проверить размер файла (макс. 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }

            // Проверить, что RFC можно редактировать (не в финальных статусах)
            if (rfc.getStatus() == RfcStatus.IMPLEMENTED ||
                    rfc.getStatus() == RfcStatus.CANCELLED) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(null); // Нельзя изменять RFC в финальных статусах
            }

            // Загрузить файл
            String description = request != null ? request.getDescription() : null;
            var attachment = attachmentService.uploadAttachment(
                    rfc, file, description, currentUser.getUsername());

            RfcAttachmentResponse response = attachmentMapper.toResponse(attachment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("Failed to upload attachment for RFC {}: {}", rfcId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Скачать вложение
     */
    @GetMapping("/{attachmentId}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable UUID rfcId,
            @PathVariable UUID attachmentId) {

        log.info("Downloading attachment: {} for RFC: {}", attachmentId, rfcId);

        try {
            // Проверить существование RFC и принадлежность вложения
            if (!rfcService.existsById(rfcId) ||
                    !attachmentService.existsById(attachmentId)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = attachmentService.downloadAttachment(attachmentId);
            var attachment = attachmentService.findById(attachmentId).orElseThrow();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, attachment.getMimeType())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(attachment.getFileSize()))
                    .body(resource);

        } catch (RuntimeException e) {
            log.error("Failed to download attachment {}: {}", attachmentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удалить вложение
     */
    @DeleteMapping("/{attachmentId}")
    @PreAuthorize("hasAnyRole('CAB_MANAGER', 'ADMIN') or @rfcSecurityService.isRequester(#rfcId, authentication)")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID rfcId,
            @PathVariable UUID attachmentId,
            Authentication authentication) {

        log.info("Deleting attachment: {} for RFC: {}", attachmentId, rfcId);

        try {
            // Проверить существование RFC
            Rfc rfc = rfcService.findById(rfcId)
                    .orElseThrow(() -> new RuntimeException("RFC not found"));

            // Проверить существование вложения
            if (!attachmentService.existsById(attachmentId)) {
                return ResponseEntity.notFound().build();
            }

            // Проверить, что RFC можно редактировать (не в финальных статусах)
            if (rfc.getStatus() == RfcStatus.IMPLEMENTED ||
                    rfc.getStatus() == RfcStatus.CANCELLED) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            attachmentService.deleteAttachment(attachmentId);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("Failed to delete attachment {}: {}", attachmentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}