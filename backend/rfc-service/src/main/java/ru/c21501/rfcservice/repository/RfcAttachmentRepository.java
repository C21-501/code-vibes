package ru.c21501.rfcservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.c21501.rfcservice.model.entity.Rfc;
import ru.c21501.rfcservice.model.entity.RfcAttachment;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с вложениями RFC
 */
@Repository
public interface RfcAttachmentRepository extends JpaRepository<RfcAttachment, UUID> {

    /**
     * Найти вложения по RFC
     */
    List<RfcAttachment> findByRfc(Rfc rfc);

    /**
     * Найти вложения по ID RFC
     */
    List<RfcAttachment> findByRfcId(UUID rfcId);

    /**
     * Проверить существование вложения по ID и RFC
     */
    boolean existsByIdAndRfcId(UUID id, UUID rfcId);

    /**
     * Удалить вложения по ID RFC
     */
    void deleteByRfcId(UUID rfcId);
}