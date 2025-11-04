package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность системы
 */
@Entity
@Table(name = "system", indexes = {
        @Index(name = "idx_system_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "subsystems")
@EqualsAndHashCode(of = "id")
public class SystemEntity {

    /**
     * Уникальный идентификатор системы
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название системы
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Описание системы
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Список подсистем
     */
    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubsystemEntity> subsystems = new ArrayList<>();

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