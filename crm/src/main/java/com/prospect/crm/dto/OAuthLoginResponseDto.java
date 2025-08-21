package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginResponseDto {
    private String action; // LOGIN, REGISTER
    private String message;
    private OAuthUserInfoDto userInfo;
    private String authorizationUrl; // OAuth authorization URL (register durumunda)
    private String state; // OAuth state parameter
    private Boolean requiresAdditionalInfo; // Telefon ve şifre gerekiyor mu
} 