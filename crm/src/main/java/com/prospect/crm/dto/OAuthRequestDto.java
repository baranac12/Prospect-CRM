package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthRequestDto {
    private String provider; // GOOGLE, MICROSOFT
    private String email;
    private String redirectUri;
} 