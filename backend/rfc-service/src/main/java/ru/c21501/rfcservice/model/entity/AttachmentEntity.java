package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * Сущность прикрепленного файла
 */
@Entity
@Table(name = "rfc_attachment", indexes = {
        @Index(name = "idx_rfc_attachment_rfc_id", columnList = "rfc_id"),
        @Index(name = "idx_rfc_attachment_uploaded_by_id", columnList = "uploaded_by_id"),
        @Index(name = "idx_rfc_attachment_create_datetime", columnList = "create_datetime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"uploadedBy", "fileData"})
@EqualsAndHashCode(of = "id")
public class AttachmentEntity {

    /**
     * Уникальный идентификатор вложения
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * RFC, к которому прикреплен файл
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_id")
    private RfcEntity rfc;

    /**
     * Оригинальное имя файла
     */
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    /**
     * Размер файла в байтах (максимум 5MB = 5242880 байт)
     */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * MIME тип файла
     */
    @Column(name = "content_type", length = 255)
    private String contentType;

    /**
     * Данные файла (хранятся в БД, максимум 5MB)
     */
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;

    /**
     * Пользователь, загрузивший файл
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private UserEntity uploadedBy;

    /**
     * Дата и время загрузки файла
     */
    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;
}
