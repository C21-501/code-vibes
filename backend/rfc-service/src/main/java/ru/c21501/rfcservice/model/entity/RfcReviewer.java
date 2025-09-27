package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Связь многие-ко-многим между RFC и командой-согласующим
 */
@Entity
@Table(name = "rfc_reviewers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"rfc_id", "team_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfcReviewer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_id", nullable = false)
    @NotNull(message = "RFC не может быть пустым")
    private Rfc rfc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @NotNull(message = "Команда не может быть пустой")
    private Team team;
}
