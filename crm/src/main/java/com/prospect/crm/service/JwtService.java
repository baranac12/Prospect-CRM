package com.prospect.crm.service;

import com.prospect.crm.config.JwtConfig;
import com.prospect.crm.constant.TokenType;
import com.prospect.crm.model.JwtToken;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.JwtTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class JwtService {

    private final JwtConfig jwtConfig;
    private final JwtTokenRepository jwtTokenRepository;
    private final SystemLogService systemLogService;

    public JwtService(JwtConfig jwtConfig, JwtTokenRepository jwtTokenRepository, SystemLogService systemLogService) {
        this.jwtConfig = jwtConfig;
        this.jwtTokenRepository = jwtTokenRepository;
        this.systemLogService = systemLogService;
    }

    public String generateAccessToken(Users user) {
        return generateToken(user, TokenType.ACCESS, jwtConfig.getAccessTokenExpiration());
    }

    public String generateRefreshToken(Users user) {
        return generateToken(user, TokenType.REFRESH, jwtConfig.getRefreshTokenExpiration());
    }

    private String generateToken(Users user, TokenType tokenType, long expirationSeconds) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + (expirationSeconds * 1000));

            String token = Jwts.builder()
                    .claims()
                    .add("sub", String.valueOf(user.getId()))
                    .add("iss", jwtConfig.getIssuer())
                    .add("aud", jwtConfig.getAudience())
                    .issuedAt(now)
                    .expiration(expiryDate)
                    .add("userId", user.getId())
                    .add("email", user.getEmail())
                    .add("username", user.getUsername())
                    .add("tokenType", tokenType.name())
                    .and()
                    .signWith(key, Jwts.SIG.HS256)
                    .compact();

            saveToken(user, token, tokenType, expiryDate);

            systemLogService.logSecurity(
                    "JWT token generated",
                    "Token type: " + tokenType + ", User: " + user.getEmail(),
                    user.getId().toString(),
                    null,
                    null
            );

            return token;

        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage(), e);
            systemLogService.logError(
                    "JWT token generation failed",
                    "User: " + user.getEmail() + ", Error: " + e.getMessage(),
                    Arrays.toString(e.getStackTrace()),
                    "JwtService",
                    "generateToken"
            );
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    private void saveToken(Users user, String token, TokenType tokenType, Date expiryDate) {
        try {
            JwtToken jwtToken = new JwtToken();
            jwtToken.setUserId(user);
            jwtToken.setTokenType(tokenType.name());
            jwtToken.setIssuedAt(LocalDateTime.now());
            jwtToken.setExpiresAt(expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            jwtToken.setRevoked(false);
            jwtToken.setExpired(false);

            if (tokenType == TokenType.ACCESS) {
                jwtToken.setAccessToken(token);
            } else {
                jwtToken.setRefreshToken(token);
            }

            jwtTokenRepository.save(jwtToken);

        } catch (Exception e) {
            log.error("Error saving JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save JWT token", e);
        }
    }

    public Claims validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!isTokenActive(token)) {
                throw new JwtException("Token is revoked or expired in database");
            }

            return claims;

        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            systemLogService.logSecurity(
                    "JWT token expired",
                    "Token: " + token.substring(0, Math.min(token.length(), 20)) + "...",
                    null,
                    null,
                    null
            );
            throw new JwtException("Token expired");
        } catch (JwtException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            systemLogService.logSecurity(
                    "JWT token validation failed",
                    "Token: " + token.substring(0, Math.min(token.length(), 20)) + "..., Error: " + e.getMessage(),
                    null,
                    null,
                    null
            );
            throw e;
        }
    }

    private boolean isTokenActive(String token) {
        Optional<JwtToken> accessToken = jwtTokenRepository.findByAccessToken(token);
        Optional<JwtToken> refreshToken = jwtTokenRepository.findByRefreshToken(token);

        JwtToken jwtToken = accessToken.orElse(refreshToken.orElse(null));

        if (jwtToken == null) {
            return false;
        }

        return !jwtToken.getRevoked() && !jwtToken.getExpired() &&
                jwtToken.getExpiresAt().isAfter(LocalDateTime.now());
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("userId", Long.class);
    }

    public boolean isAccessToken(String token) {
        try {
            Claims claims = validateToken(token);
            String tokenType = claims.get("tokenType", String.class);
            return TokenType.ACCESS.name().equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = validateToken(token);
            String tokenType = claims.get("tokenType", String.class);
            return TokenType.REFRESH.name().equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    public void revokeAllUserTokens(Users user) {
        try {
            jwtTokenRepository.revokeAllUserTokens(user);

            systemLogService.logSecurity(
                    "All user tokens revoked",
                    "User: " + user.getEmail(),
                    user.getId().toString(),
                    null,
                    null
            );

        } catch (Exception e) {
            log.error("Error revoking user tokens: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to revoke user tokens", e);
        }
    }

    public void revokeToken(String accessToken, String refreshToken) {
        try {
            jwtTokenRepository.revokeToken(accessToken, refreshToken);

            systemLogService.logSecurity(
                    "Token revoked",
                    "Access token: " + (accessToken != null ? accessToken.substring(0, Math.min(accessToken.length(), 20)) + "..." : "null"),
                    null,
                    null,
                    null
            );

        } catch (Exception e) {
            log.error("Error revoking token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to revoke token", e);
        }
    }

    public void markExpiredTokens() {
        try {
            jwtTokenRepository.markExpiredTokens(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error marking expired tokens: {}", e.getMessage(), e);
        }
    }
} 