package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Сущность подсистемы
 */
@Entity
@Table(name = "subsystem", indexes = {
        @Index(name = "idx_subsystem_system_id", columnList = "system_id"),
        @Index(name = "idx_subsystem_team_id", columnList = "team_id"),
        @Index(name = "idx_subsystem_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"system", "team"})
@EqualsAndHashCode(of = "id")
public class SubsystemEntity {

    /**
     * Уникальный идентификатор подсистемы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название подсистемы
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Описание подсистемы
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Родительская система
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", nullable = false)
    private SystemEntity system;

    /**
     * Ответственная команда
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity team;

    /**
     * Дата и время создания записи
     */
    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;

    /**
     * Дата и время последнего обновления записи
     */
    @UpdateTimestamp
    @Column(name = "update_datetime", nullable = false)
    private OffsetDateTime updateDatetime;
}