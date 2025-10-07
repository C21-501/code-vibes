package ru.c21501.rfcservice.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcAttachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с вложениями RFC
 */
public interface RfcAttachmentService {

    /**
     * Загрузить вложение для RFC
     */
    RfcAttachment uploadAttachment(Rfc rfc, MultipartFile file, String description, String uploadedBy);

    /**
     * Найти вложение по ID
     */
    Optional<RfcAttachment> findById(UUID id);

    /**
     * Найти вложения по RFC
     */
    List<RfcAttachment> findByRfc(Rfc rfc);

    /**
     * Найти вложения по ID RFC
     */
    List<RfcAttachment> findByRfcId(UUID rfcId);

    /**
     * Скачать вложение как Resource
     */
    Resource downloadAttachment(UUID attachmentId);

    /**
     * Удалить вложение
     */
    void deleteAttachment(UUID attachmentId);

    /**
     * Удалить все вложения RFC
     */
    void deleteAllByRfcId(UUID rfcId);

    /**
     * Проверить существование вложения
     */
    boolean existsById(UUID id);

    /**
     * Получить общий размер вложений RFC
     */
    Long getTotalSizeByRfcId(UUID rfcId);
}
