package ru.c21501.rfcservice.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.dto.response.RfcAttachmentResponse;
import ru.c21501.rfcservice.mapper.RfcAttachmentMapper;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.service.RfcAttachmentService;
import ru.c21501.rfcservice.service.RfcService;

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
    private final RfcAttachmentMapper attachmentMapper;

    /**
     * Получить список вложений RFC
     */
    @GetMapping
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
    public ResponseEntity<RfcAttachmentResponse> uploadAttachment(
            @PathVariable UUID rfcId,
            @RequestPart("file") MultipartFile file) {

        log.info("Uploading attachment for RFC: {}, file: {}", rfcId, file.getOriginalFilename());

        try {
            // Получить RFC
            Rfc rfc = rfcService.findById(rfcId)
                    .orElseThrow(() -> new RuntimeException("RFC not found"));

            // Временно используем фиксированного пользователя (как в RfcController)
            String uploadedBy = "system-user";

            // Проверить размер файла (макс. 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
            }

            // Проверить, что RFC можно редактировать (не в финальных статусах)
            if (rfc.getStatus() == RfcStatus.IMPLEMENTED ||
                    rfc.getStatus() == RfcStatus.CANCELLED) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }

            // Загрузить файл БЕЗ описания
            var attachment = attachmentService.uploadAttachment(
                    rfc, file, null, uploadedBy); // description = null

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
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable UUID rfcId,
            @PathVariable UUID attachmentId) {

        log.info("Downloading attachment: {} for RFC: {}", attachmentId, rfcId);

        try {
            if (!rfcService.existsById(rfcId) || !attachmentService.existsById(attachmentId)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = attachmentService.downloadAttachment(attachmentId);
            var attachment = attachmentService.findById(attachmentId).orElseThrow();

            // Кодируем имя файла
            String contentDisposition = createContentDisposition(attachment.getOriginalFileName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .header(HttpHeaders.CONTENT_TYPE, attachment.getMimeType())
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(attachment.getFileSize()))
                    .body(resource);

        } catch (RuntimeException e) {
            log.error("Failed to download attachment {}: {}", attachmentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String createContentDisposition(String fileName) {
        try {
            String safeFileName = transliterateRussian(fileName);
            // Дополнительно очищаем от любых оставшихся не-ASCII символов
            safeFileName = safeFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            return "attachment; filename=\"" + safeFileName + "\"";
        } catch (Exception e) {
            log.warn("Failed to transliterate filename: {}, using safe name", fileName);
            return "attachment; filename=\"document\"";
        }
    }

    private String transliterateRussian(String text) {
        if (text == null) return "";

        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(transliterateChar(c));
        }
        return result.toString();
    }

    private String transliterateChar(char c) {
        switch (c) {
            // Русские заглавные буквы
            case 'А': return "A";
            case 'Б': return "B";
            case 'В': return "V";
            case 'Г': return "G";
            case 'Д': return "D";
            case 'Е': return "E";
            case 'Ё': return "E";
            case 'Ж': return "Zh";
            case 'З': return "Z";
            case 'И': return "I";
            case 'Й': return "Y";
            case 'К': return "K";
            case 'Л': return "L";
            case 'М': return "M";
            case 'Н': return "N";
            case 'О': return "O";
            case 'П': return "P";
            case 'Р': return "R";
            case 'С': return "S";
            case 'Т': return "T";
            case 'У': return "U";
            case 'Ф': return "F";
            case 'Х': return "Kh";
            case 'Ц': return "Ts";
            case 'Ч': return "Ch";
            case 'Ш': return "Sh";
            case 'Щ': return "Sch";
            case 'Ъ': return "";
            case 'Ы': return "Y";
            case 'Ь': return "";
            case 'Э': return "E";
            case 'Ю': return "Yu";
            case 'Я': return "Ya";

            // Русские строчные буквы
            case 'а': return "a";
            case 'б': return "b";
            case 'в': return "v";
            case 'г': return "g";
            case 'д': return "d";
            case 'е': return "e";
            case 'ё': return "e";
            case 'ж': return "zh";
            case 'з': return "z";
            case 'и': return "i";
            case 'й': return "y";
            case 'к': return "k";
            case 'л': return "l";
            case 'м': return "m";
            case 'н': return "n";
            case 'о': return "o";
            case 'п': return "p";
            case 'р': return "r";
            case 'с': return "s";
            case 'т': return "t";
            case 'у': return "u";
            case 'ф': return "f";
            case 'х': return "kh";
            case 'ц': return "ts";
            case 'ч': return "ch";
            case 'ш': return "sh";
            case 'щ': return "sch";
            case 'ъ': return "";
            case 'ы': return "y";
            case 'ь': return "";
            case 'э': return "e";
            case 'ю': return "yu";
            case 'я': return "ya";

            // Специальные символы - заменяем на аналоги
            case ' ': return "_";
            case ',': return "_";
            case ';': return "_";
            case ':': return "_";
            case '!': return "_";
            case '?': return "_";
            case '"': return "_";
            case '\'': return "_";
            case '`': return "_";
            case '~': return "_";
            case '@': return "_";
            case '#': return "_";
            case '$': return "_";
            case '%': return "_";
            case '^': return "_";
            case '&': return "_";
            case '*': return "_";
            case '=': return "_";
            case '+': return "_";
            case '|': return "_";
            case '\\': return "_";
            case '/': return "_";
            case '<': return "_";
            case '>': return "_";
            case '[': return "_";
            case ']': return "_";
            case '{': return "_";
            case '}': return "_";

            // Скобки можно оставить или заменить
            case '(': return "(";
            case ')': return ")";

            default:
                // Оставляем латинские буквы, цифры, точку, дефис и подчеркивание как есть
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
                        (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '_') {
                    return String.valueOf(c);
                }
                // Все остальные символы (включая украинские, казахские и т.д.) заменяем на подчеркивание
                return "_";
        }
    }

    /**
     * Удалить вложение
     */
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable UUID rfcId,
            @PathVariable UUID attachmentId) {

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