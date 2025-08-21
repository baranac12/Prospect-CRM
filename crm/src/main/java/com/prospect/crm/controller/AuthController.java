package com.prospect.crm.controller;

import com.prospect.crm.config.JwtConfig;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.AuthRequestDto;
import com.prospect.crm.dto.AuthResponseDto;
import com.prospect.crm.dto.RefreshTokenRequestDto;
import com.prospect.crm.exception.AuthenticationException;
import com.prospect.crm.service.HybridAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    
    private final HybridAuthService hybridAuthService;
    private final JwtConfig jwtConfig;
    
    public AuthController(HybridAuthService hybridAuthService, JwtConfig jwtConfig) {
        this.hybridAuthService = hybridAuthService;
        this.jwtConfig = jwtConfig;
    }
    
    /**
     * Kullanıcı girişi
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto authRequestDto,
                                                             HttpServletResponse response) {
        try {
            AuthResponseDto authResponse = hybridAuthService.authenticate(authRequestDto);
            
            // Token'ları cookie'ye set et
            setAuthCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
            
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication failed", "ERR_2002", e.getMessage()));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Login failed", "ERR_1006", "Internal server error"));
        }
    }
    
    /**
     * Token yenileme
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto,
                                                                   HttpServletResponse response) {
        try {
            AuthResponseDto authResponse = hybridAuthService.refreshToken(refreshTokenRequestDto);
            
            // Yeni token'ları cookie'ye set et
            setAuthCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());
            
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Token refreshed successfully"));
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token refresh failed", "ERR_2002", e.getMessage()));
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Token refresh failed", "ERR_1006", "Internal server error"));
        }
    }
    
    /**
     * Kullanıcı çıkışı
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@CookieValue(value = "access_token", required = false) String accessToken,
                                                     @CookieValue(value = "refresh_token", required = false) String refreshToken,
                                                     HttpServletResponse response) {
        try {
            // Token'ları iptal et
            hybridAuthService.logout(accessToken, refreshToken);
            
            // Cookie'leri temizle
            clearAuthCookies(response);
            
            return ResponseEntity.ok(ApiResponse.success("Logout successful", "Logout successful"));
            
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Logout failed", "ERR_1006", "Internal server error"));
        }
    }
    
    /**
     * Mevcut kullanıcı bilgilerini getir
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponseDto.UserInfoDto>> getCurrentUser(@CookieValue(value = "access_token", required = false) String accessToken) {
        try {
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("No access token provided", "ERR_2003", "Authentication required"));
            }
            
            var user = hybridAuthService.validateTokenAndGetUser(accessToken);
            
            AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(), // firstName
                user.getSurname()  // lastName
            );
            
            return ResponseEntity.ok(ApiResponse.success(userInfo, "User information retrieved successfully"));
            
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication failed", "ERR_2002", e.getMessage()));
        } catch (Exception e) {
            log.error("Get current user error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get user information", "ERR_1006", "Internal server error"));
        }
    }
    
    /**
     * Authentication cookie'lerini set eder
     */
    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        // Access token cookie
        Cookie accessCookie = new Cookie(jwtConfig.getAccessTokenCookieName(), accessToken);
        accessCookie.setPath(jwtConfig.getCookiePath());
        if (jwtConfig.getCookieDomain() != null && !jwtConfig.getCookieDomain().isEmpty()) {
            accessCookie.setDomain(jwtConfig.getCookieDomain());
        }
        accessCookie.setMaxAge((int) jwtConfig.getAccessTokenExpiration());
        accessCookie.setSecure(jwtConfig.isCookieSecure());
        accessCookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(accessCookie);
        
        // Refresh token cookie
        Cookie refreshCookie = new Cookie(jwtConfig.getRefreshTokenCookieName(), refreshToken);
        refreshCookie.setPath(jwtConfig.getCookiePath());
        if (jwtConfig.getCookieDomain() != null && !jwtConfig.getCookieDomain().isEmpty()) {
            refreshCookie.setDomain(jwtConfig.getCookieDomain());
        }
        refreshCookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration());
        refreshCookie.setSecure(jwtConfig.isCookieSecure());
        refreshCookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(refreshCookie);
    }
    
    /**
     * Authentication cookie'lerini temizler
     */
    private void clearAuthCookies(HttpServletResponse response) {
        // Access token cookie'sini temizle
        Cookie accessCookie = new Cookie(jwtConfig.getAccessTokenCookieName(), null);
        accessCookie.setPath(jwtConfig.getCookiePath());
        if (jwtConfig.getCookieDomain() != null && !jwtConfig.getCookieDomain().isEmpty()) {
            accessCookie.setDomain(jwtConfig.getCookieDomain());
        }
        accessCookie.setMaxAge(0);
        accessCookie.setSecure(jwtConfig.isCookieSecure());
        accessCookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(accessCookie);
        
        // Refresh token cookie'sini temizle
        Cookie refreshCookie = new Cookie(jwtConfig.getRefreshTokenCookieName(), null);
        refreshCookie.setPath(jwtConfig.getCookiePath());
        if (jwtConfig.getCookieDomain() != null && !jwtConfig.getCookieDomain().isEmpty()) {
            refreshCookie.setDomain(jwtConfig.getCookieDomain());
        }
        refreshCookie.setMaxAge(0);
        refreshCookie.setSecure(jwtConfig.isCookieSecure());
        refreshCookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(refreshCookie);
    }
} 