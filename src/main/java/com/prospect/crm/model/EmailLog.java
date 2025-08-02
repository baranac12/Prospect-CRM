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
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users usersId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id")
    private EmailDraft draftId;

    @Column(name = "recipient_email", length = Integer.MAX_VALUE)
    private String recipientEmail;

    @Column(name = "status", length = Integer.MAX_VALUE)
    private String status;

    @Column(name = "response_received")
    private Boolean responseReceived;

    @Column(name = "error_message", length = Integer.MAX_VALUE)
    private String errorMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

}