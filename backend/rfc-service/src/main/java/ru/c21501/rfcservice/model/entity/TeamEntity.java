package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность команды
 */
@Entity
@Table(name = "team", indexes = {
        @Index(name = "idx_team_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "members")
@EqualsAndHashCode(of = "id")
public class TeamEntity {

    /**
     * Уникальный идентификатор команды
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название команды
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Описание команды
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Список членов команды
     */
    @ManyToMany
    @JoinTable(
            name = "team_member",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<UserEntity> members = new HashSet<>();

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
