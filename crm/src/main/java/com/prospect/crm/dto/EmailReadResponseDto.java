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
public class EmailReadResponseDto {
    private String id;
    private String threadId;
    private String subject;
    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String body;
    private String contentType; // text/plain, text/html
    private LocalDateTime receivedDate;
    private LocalDateTime sentDate;
    private Boolean isRead;
    private Boolean isStarred;
    private Boolean isImportant;
    private List<String> labels;
    private List<EmailAttachmentDto> attachments;
    private String snippet; // Email özeti
    private String provider; // GOOGLE, MICROSOFT
} 