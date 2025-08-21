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
public class OAuthResponseDto {
    private String provider;
    private String email;
    private String status; // CONNECTED, DISCONNECTED, EXPIRED
    private LocalDateTime connectedAt;
    private LocalDateTime expiresAt;
    private List<String> scopes;
    private String accessToken;
    private String refreshToken;
} 