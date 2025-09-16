package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.c21501.rfcservice.model.enums.RfcStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * История изменений статусов RFC
 */
@Entity
@Table(name = "status_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_id", nullable = false)
    @NotNull(message = "RFC не может быть пустым")
    private Rfc rfc;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private RfcStatus oldStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    @NotNull(message = "Новый статус не может быть пустым")
    private RfcStatus newStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    @NotNull(message = "Пользователь, изменивший статус, не может быть пустым")
    private User changedByUser;
    
    @Column(name = "change_date", nullable = false)
    @NotNull(message = "Дата изменения не может быть пустой")
    private LocalDateTime changeDate;
    
    @PrePersist
    protected void onCreate() {
        if (changeDate == null) {
            changeDate = LocalDateTime.now();
        }
    }
}
