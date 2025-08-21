package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAttachmentDto {
    private String fileName;
    private String contentType;
    private byte[] content;
    private String base64Content; // Alternative to byte array
} 