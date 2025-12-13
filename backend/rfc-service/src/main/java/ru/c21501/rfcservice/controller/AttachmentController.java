package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.openapi.api.AttachmentsApi;
import ru.c21501.rfcservice.openapi.model.AttachmentResponse;
import ru.c21501.rfcservice.service.AttachmentApiService;

/**
 * Контроллер для загрузки и скачивания файлов
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AttachmentController implements AttachmentsApi {

    private final AttachmentApiService attachmentApiService;

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse uploadAttachment(MultipartFile file) {
        log.info("POST /attachment - Uploading file: {}, size: {} bytes",
                file.getOriginalFilename(), file.getSize());
        return attachmentApiService.uploadAttachment(file);
    }

    @Override
    public Resource downloadAttachment(Long id) {
        log.info("GET /attachment/{} - Downloading file", id);
        return attachmentApiService.downloadAttachment(id);
    }
}