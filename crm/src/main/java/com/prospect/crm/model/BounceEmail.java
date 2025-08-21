package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bounce_emails")
public class BounceEmail {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bounce_emails_seq")
    @SequenceGenerator(name = "bounce_emails_seq", sequenceName = "bounce_emails_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead leadId;

    private String emailAddress;
    private String bounceType; // HARD_BOUNCE, SOFT_BOUNCE, BLOCKED, SPAM
    private String bounceReason; // User unknown, Mailbox full, etc.
    private String originalMessageId; // Original email message ID
    private String provider; // GMAIL, OUTLOOK, SMTP
    private Boolean processed; // Whether this bounce has been processed
    private LocalDateTime bounceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 