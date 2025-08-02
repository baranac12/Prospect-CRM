package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_drafts")
public class EmailDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_drafts_id_gen")
    @SequenceGenerator(name = "email_drafts_id_gen", sequenceName = "email_drafts_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users usersId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead leadId;

    @Column(name = "subject", length = Integer.MAX_VALUE)
    private String subject;

    @Column(name = "body", length = Integer.MAX_VALUE)
    private String body;

    @Column(name = "created_by_robot")
    private Boolean createdByRobot;

    @Column(name = "status", length = Integer.MAX_VALUE)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}