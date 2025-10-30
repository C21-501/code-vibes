package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.openapi.api.RfcAttachmentsApi;
import ru.c21501.rfcservice.openapi.model.AttachmentResponse;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class RfcAttachmentController implements RfcAttachmentsApi {

    @Override
    @ResponseStatus(HttpStatus.OK)
    public List<AttachmentResponse> getRfcAttachments(Long rfcId) {
        log.info("getRfcAttachments called with rfcId: {}", rfcId);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse uploadRfcAttachment(Long rfcId, MultipartFile file) {
        log.info("uploadRfcAttachment called with rfcId: {}, filename: {}, size: {}",
                 rfcId, file.getOriginalFilename(), file.getSize());
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public Resource downloadRfcAttachment(Long rfcId, Long attachmentId) {
        log.info("downloadRfcAttachment called with rfcId: {}, attachmentId: {}", rfcId, attachmentId);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRfcAttachment(Long rfcId, Long attachmentId) {
        log.info("deleteRfcAttachment called with rfcId: {}, attachmentId: {}", rfcId, attachmentId);
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
