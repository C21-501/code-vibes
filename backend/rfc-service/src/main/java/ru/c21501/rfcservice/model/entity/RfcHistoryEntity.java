package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.c21501.rfcservice.converter.LongSetConverter;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;
import ru.c21501.rfcservice.openapi.model.RfcStatus;
import ru.c21501.rfcservice.openapi.model.Urgency;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Сущность истории изменений RFC
 */
@Entity
@Table(name = "rfc_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfcHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rfc_id", nullable = false)
    private Long rfcId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, length = 20)
    private HistoryOperationType operation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private UserEntity changedBy;

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

    @Convert(converter = LongSetConverter.class)
    @Column(name = "attachment_ids", columnDefinition = "TEXT")
    private Set<Long> attachmentIds;

    @Convert(converter = LongSetConverter.class)
    @Column(name = "affected_subsystems", columnDefinition = "TEXT")
    private Set<Long> affectedSubsystems;

    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;
}