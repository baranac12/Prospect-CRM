package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDraftResponseDto {
    private Long id;
    private Long userId;
    private Long leadId;
    private String subject;
    private String body;
    private String contentType;
    private List<String> toEmails;
    private List<String> ccEmails;
    private List<String> bccEmails;
    private List<EmailAttachmentDto> attachments;
    private Boolean createdByRobot;
    private String status; // DRAFT, SENT, CANCELLED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    private String provider;
    private String templateName;
    private String templateData;
} 