package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.c21501.rfcservice.model.enums.HistoryOperationType;

import java.time.OffsetDateTime;

/**
 * Сущность истории изменений затронутых подсистем RFC
 */
@Entity
@Table(name = "rfc_affected_subsystem_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfcAffectedSubsystemHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rfc_affected_subsystem_id", nullable = false)
    private Long rfcAffectedSubsystemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false, length = 20)
    @Builder.Default
    private HistoryOperationType operation = HistoryOperationType.UPDATE;

    @Column(name = "status_type", nullable = false, length = 50)
    private String statusType;

    @Column(name = "old_status", length = 50)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 50)
    private String newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id", nullable = false)
    private UserEntity changedBy;

    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;
}