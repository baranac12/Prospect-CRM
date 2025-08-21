package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDraftListRequestDto {
    private String status; // DRAFT, SENT, CANCELLED, ALL
    private Long leadId; // Filter by lead
    private String provider; // GOOGLE, MICROSOFT, SMTP
    private String templateName; // Filter by template
    private Boolean createdByRobot; // Filter by creation type
    private String searchTerm; // Search in subject, body, emails
    private String sortBy; // createdAt, updatedAt, subject
    private String sortOrder; // ASC, DESC
    private Integer page; // Page number (0-based)
    private Integer size; // Page size
} 