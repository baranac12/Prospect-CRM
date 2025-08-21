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
public class EmailSendRequestDto {
    private String provider; // GOOGLE, MICROSOFT
    private String fromEmail;
    private List<String> toEmails;
    private List<String> ccEmails;
    private List<String> bccEmails;
    private String subject;
    private String body;
    private String contentType; // text/plain, text/html
    private List<EmailAttachmentDto> attachments;
} 