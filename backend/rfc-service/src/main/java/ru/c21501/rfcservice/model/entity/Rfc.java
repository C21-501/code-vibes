package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.c21501.rfcservice.model.enums.Priority;
import ru.c21501.rfcservice.model.enums.RfcStatus;
import ru.c21501.rfcservice.model.enums.RiskLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сущность RFC (Request for Change)
 */
@Entity
@Table(name = "rfc")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rfc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Заголовок RFC не может быть пустым")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Описание RFC не может быть пустым")
    private String description;

    @Column(name = "justification", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Обоснование RFC не может быть пустым")
    private String justification;

    @Column(name = "impact_analysis", columnDefinition = "TEXT")
    private String impactAnalysis;

    @Column(name = "rollback_plan", columnDefinition = "TEXT")
    private String rollbackPlan;

    @Column(name = "testing_plan", columnDefinition = "TEXT")
    private String testingPlan;

    @Column(name = "implementation_plan", columnDefinition = "TEXT")
    private String implementationPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Статус RFC не может быть пустым")
    private RfcStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @NotNull(message = "Приоритет RFC не может быть пустым")
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    @NotNull(message = "Уровень риска RFC не может быть пустым")
    private RiskLevel riskLevel;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "actual_duration")
    private Integer actualDuration;

    @Column(name = "planned_start_date")
    private LocalDateTime plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDateTime plannedEndDate;

    @Column(name = "actual_start_date")
    private LocalDateTime actualStartDate;

    @Column(name = "actual_end_date")
    private LocalDateTime actualEndDate;

    @Column(name = "created_at", nullable = false)
    @NotNull(message = "Дата создания RFC не может быть пустой")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull(message = "Дата обновления RFC не может быть пустой")
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "implemented_at")
    private LocalDateTime implementedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @NotNull(message = "Создатель RFC не может быть пустым")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @NotNull(message = "Заявитель RFC не может быть пустым")
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id")
    private System system;

    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfcExecutor> executors;

    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfcReviewer> reviewers;

    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusHistory> statusHistory;

    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfcAttachment> attachments;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RfcStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
