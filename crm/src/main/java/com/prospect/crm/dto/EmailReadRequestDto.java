package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailReadRequestDto {
    private String provider; // GOOGLE, MICROSOFT
    private String emailId; // Email ID'si
    private String format; // full, minimal, raw
    private Boolean includeAttachments; // Ekleri dahil et
} 