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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead leadId;

    private String subject;
    private String body;
    private String contentType; // text/plain, text/html
    private String toEmails; // Comma-separated email addresses
    private String ccEmails; // Comma-separated email addresses
    private String bccEmails; // Comma-separated email addresses
    private String attachments; // JSON string of attachment info
    private Boolean createdByRobot;
    private String status; // DRAFT, SENT, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    private String provider; // GOOGLE, MICROSOFT, SMTP
    private String templateName; // If using email template
    private String templateData; // JSON string of template variables
}