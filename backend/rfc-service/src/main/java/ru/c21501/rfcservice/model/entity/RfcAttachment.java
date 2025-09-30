package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность для вложений RFC
 */
@Entity
@Table(name = "rfc_attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfcAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_id", nullable = false)
    @NotNull(message = "RFC не может быть пустым")
    private Rfc rfc;

    @Column(name = "file_name", nullable = false)
    @NotBlank(message = "Имя файла не может быть пустым")
    private String fileName;

    @Column(name = "original_file_name", nullable = false)
    @NotBlank(message = "Оригинальное имя файла не может быть пустым")
    private String originalFileName;

    @Column(name = "file_size", nullable = false)
    @NotNull(message = "Размер файла не может быть пустым")
    private Long fileSize;

    @Column(name = "mime_type", nullable = false)
    @NotBlank(message = "MIME-тип не может быть пустым")
    private String mimeType;

    @Column(name = "file_path", nullable = false)
    @NotBlank(message = "Путь к файлу не может быть пустым")
    private String filePath;

    @Column(name = "description")
    private String description;

    @Column(name = "uploaded_by", nullable = false)
    @NotBlank(message = "Пользователь загрузки не может быть пустым")
    private String uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    @NotNull(message = "Дата загрузки не может быть пустой")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }
}
