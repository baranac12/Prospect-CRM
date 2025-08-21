package com.prospect.crm.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private String secret;
    private String issuer;
    private String audience;
    
    // Access token ayarları
    private long accessTokenExpiration;
    
    // Refresh token ayarları
    private long refreshTokenExpiration;
    
    // Cookie ayarları
    private String accessTokenCookieName;
    private String refreshTokenCookieName;
    private String cookieDomain;
    private String cookiePath;
    private boolean cookieSecure;
    private boolean cookieHttpOnly;
    private int cookieMaxAge;
} 