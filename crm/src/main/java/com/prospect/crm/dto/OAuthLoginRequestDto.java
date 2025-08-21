package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginRequestDto {
    private String provider; // google, microsoft
    private String email;
    private String password; // Yeni kullanıcı için şifre (register durumunda)
    private String phone; // Yeni kullanıcı için telefon (opsiyonel)
    private String redirectUri;
} 