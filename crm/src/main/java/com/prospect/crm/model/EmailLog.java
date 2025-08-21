package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_logs")
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_logs_id_gen")
    @SequenceGenerator(name = "email_logs_id_gen", sequenceName = "email_logs_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id")
    private EmailDraft draftId;

    private String recipientEmail;
    private String status;
    private Boolean responseReceived;
    private String errorMessage;
    private LocalDateTime sentAt;
}