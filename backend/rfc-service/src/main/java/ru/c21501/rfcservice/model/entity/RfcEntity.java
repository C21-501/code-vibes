package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.c21501.rfcservice.openapi.model.RfcStatus;
import ru.c21501.rfcservice.openapi.model.Urgency;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность RFC (Request for Change)
 */
@Entity
@Table(name = "rfc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfcEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "implementation_date", nullable = false)
    private OffsetDateTime implementationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false, length = 50)
    private Urgency urgency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private RfcStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @Column(name = "deleted_datetime")
    private OffsetDateTime deletedDatetime;

    @Column(name = "planka_card_id")
    private String plankaCardId;

    /**
     * Временная метка последнего изменения статуса из Planka.
     * Используется чтобы scheduler не перезаписывал статус в течение определённого времени.
     */
    @Column(name = "planka_status_changed_at")
    private OffsetDateTime plankaStatusChangedAt;

    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;

    @UpdateTimestamp
    @Column(name = "update_datetime", nullable = false)
    private OffsetDateTime updateDatetime;

    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RfcAffectedSubsystemEntity> affectedSubsystems = new ArrayList<>();

    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentEntity> attachments = new ArrayList<>();
}