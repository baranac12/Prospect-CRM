package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDraftRequestDto {
    private Long leadId; // Optional: if draft is related to a lead
    private String subject;
    private String body;
    private String contentType; // text/plain, text/html
    private List<String> toEmails;
    private List<String> ccEmails;
    private List<String> bccEmails;
    private List<EmailAttachmentDto> attachments;
    private String provider; // GOOGLE, MICROSOFT, SMTP
    private String templateName; // If using email template
    private String templateData; // JSON string of template variables
} 