package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.c21501.rfcservice.openapi.model.ConfirmationStatus;
import ru.c21501.rfcservice.openapi.model.ExecutionStatus;

import java.time.OffsetDateTime;

/**
 * Сущность связи RFC с затронутой подсистемой и исполнителем
 */
@Entity
@Table(name = "rfc_affected_subsystem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfcAffectedSubsystemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_id", nullable = false)
    private RfcEntity rfc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subsystem_id", nullable = false)
    private SubsystemEntity subsystem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    private UserEntity executor;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_status", nullable = false, length = 50)
    @Builder.Default
    private ConfirmationStatus confirmationStatus = ConfirmationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status", nullable = false, length = 50)
    @Builder.Default
    private ExecutionStatus executionStatus = ExecutionStatus.PENDING;

    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;

    @UpdateTimestamp
    @Column(name = "update_datetime", nullable = false)
    private OffsetDateTime updateDatetime;
}