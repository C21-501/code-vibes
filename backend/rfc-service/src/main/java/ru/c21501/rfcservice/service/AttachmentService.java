package ru.c21501.rfcservice.service;

import org.springframework.web.multipart.MultipartFile;
import ru.c21501.rfcservice.model.entity.AttachmentEntity;
import ru.c21501.rfcservice.model.entity.UserEntity;

import java.util.List;

/**
 * Сервис для работы с вложениями (бизнес-логика)
 */
public interface AttachmentService {

    /**
     * Загружает новый файл без привязки к RFC
     *
     * @param file         файл
     * @param uploadedBy пользователь, загрузивший файл
     * @return созданное вложение
     */
    AttachmentEntity uploadAttachment(MultipartFile file, UserEntity uploadedBy);

    /**
     * Получает вложение по ID
     *
     * @param id ID вложения
     * @return вложение
     */
    AttachmentEntity getAttachmentById(Long id);

    /**
     * Получает список вложений для RFC
     *
     * @param rfcId ID RFC
     * @return список вложений
     */
    List<AttachmentEntity> getAttachmentsByRfcId(Long rfcId);

    /**
     * Привязывает вложения к RFC
     *
     * @param attachmentIds список ID вложений
     * @param rfcId         ID RFC
     */
    void attachToRfc(List<Long> attachmentIds, Long rfcId);

    /**
     * Резолвит список вложений по их ID
     *
     * @param attachmentIds список ID вложений
     * @return список сущностей вложений
     */
    List<AttachmentEntity> resolveAttachments(List<Long> attachmentIds);
}
