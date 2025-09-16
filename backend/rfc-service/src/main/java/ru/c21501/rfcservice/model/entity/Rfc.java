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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность RFC (Request for Change)
 */
@Entity
@Table(name = "rfcs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rfc {
    
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @NotBlank(message = "ID RFC не может быть пустым")
    private String id; // например "RFC-0042"
    
    @Column(name = "title", nullable = false)
    @NotBlank(message = "Заголовок RFC не может быть пустым")
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Статус RFC не может быть пустым")
    private RfcStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @NotNull(message = "Приоритет RFC не может быть пустым")
    private Priority priority;
    
    @Column(name = "planned_date")
    private LocalDate plannedDate;
    
    @Column(name = "created_date", nullable = false)
    @NotNull(message = "Дата создания RFC не может быть пустой")
    private LocalDateTime createdDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    @NotNull(message = "Инициатор RFC не может быть пустым")
    private User initiator;
    
    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfcExecutor> executors;
    
    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RfcReviewer> reviewers;
    
    @OneToMany(mappedBy = "rfc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StatusHistory> statusHistory;
    
    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (status == null) {
            status = RfcStatus.REQUESTED_NEW;
        }
    }
}
