package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Сущность подсистемы
 */
@Entity
@Table(name = "systems")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class System {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "Название подсистемы не может быть пустым")
    private String name;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "description")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_team_id", nullable = false)
    @NotNull(message = "Ответственная команда не может быть пустой")
    private Team responsibleTeam;
}
