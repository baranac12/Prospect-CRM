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
public class EmailDraftListItemDto {
    private Long id;
    private Long userId;
    private Long leadId;
    private String subject;
    private String body;
    private String contentType;
    private List<String> toEmails;
    private List<String> ccEmails;
    private List<String> bccEmails;
    private Boolean createdByRobot;
    private String status;
    private String provider;
    private String templateName;
    private String createdAt;
    private String updatedAt;
    private String sentAt;
    private Boolean hasAttachments;
} 