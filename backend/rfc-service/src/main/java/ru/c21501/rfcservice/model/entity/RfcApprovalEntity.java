package ru.c21501.rfcservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "rfc_approval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfcApprovalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfc_id", nullable = false)
    private RfcEntity rfc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private UserEntity approver;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "create_datetime", nullable = false, updatable = false)
    private OffsetDateTime createDatetime;

    @UpdateTimestamp
    @Column(name = "update_datetime", nullable = false)
    private OffsetDateTime updateDatetime;
}