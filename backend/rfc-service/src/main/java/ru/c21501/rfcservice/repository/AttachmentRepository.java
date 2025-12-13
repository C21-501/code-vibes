package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.AttachmentEntity;

import java.util.List;

/**
 * Репозиторий для работы с вложениями
 */
@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

    /**
     * Получить все непривязанные к RFC файлы
     *
     * @return список непривязанных файлов
     */
    List<AttachmentEntity> findByRfcIsNull();

    /**
     * Получить все файлы RFC
     *
     * @param rfcId ID RFC
     * @return список файлов
     */
    List<AttachmentEntity> findByRfc_Id(Long rfcId);
}