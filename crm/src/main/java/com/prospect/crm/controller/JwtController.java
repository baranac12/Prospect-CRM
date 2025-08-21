package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;

import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.JwtService;
import com.prospect.crm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/jwt")
@PreAuthorize("hasRole('USER')")
@SuppressWarnings("unused")
public class JwtController {
    
    private final JwtService jwtService;
    private final UserService userService;
    
    public JwtController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }
    
    /**
     * Token'dan kullanıcı ID'sini çıkarır
     */
    @PostMapping("/validate")
    @HasPermission(PermissionConstants.API_ACCESS)
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Token is required")
                        .build());
            }
            
            // Token'ı doğrula ve kullanıcı ID'sini al
            Long userId = jwtService.getUserIdFromToken(token);
            boolean isAccess = jwtService.isAccessToken(token);
            boolean isRefresh = jwtService.isRefreshToken(token);
            
            Map<String, Object> result = Map.of(
                "valid", true,
                "userId", userId,
                "isAccessToken", isAccess,
                "isRefreshToken", isRefresh
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Token validated successfully")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Invalid token: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Token tipini kontrol eder
     */
    @PostMapping("/check-type")
    @HasPermission(PermissionConstants.API_ACCESS)
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkTokenType(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                        .success(false)
                        .message("Token is required")
                        .build());
            }
            
            boolean isAccess = jwtService.isAccessToken(token);
            boolean isRefresh = jwtService.isRefreshToken(token);
            
            Map<String, Object> result = Map.of(
                "token", token.substring(0, Math.min(token.length(), 20)) + "...",
                "isAccessToken", isAccess,
                "isRefreshToken", isRefresh,
                "type", isAccess ? "ACCESS" : (isRefresh ? "REFRESH" : "UNKNOWN")
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Token type checked successfully")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error checking token type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Error checking token type: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Kullanıcının tüm token'larını iptal eder (Admin veya kullanıcının kendisi)
     */
    @PostMapping("/revoke-user-tokens/{userId}")
    @HasPermission(value = PermissionConstants.USER_UPDATE, any = {PermissionConstants.SYSTEM_CONFIG_UPDATE})
    public ResponseEntity<ApiResponse<String>> revokeUserTokens(@PathVariable Long userId) {
        try {
            // UserService'den Users entity'yi almak için yeni bir method gerekli
            log.info("Revoking all tokens for user: {}", userId);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Token revocation - functionality to be implemented")
                    .data("Token revocation pending for user: " + userId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error revoking user tokens for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to revoke user tokens: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Belirli token'ları iptal eder
     */
    @PostMapping("/revoke")
    @HasPermission(PermissionConstants.API_ACCESS)
    public ResponseEntity<ApiResponse<String>> revokeTokens(@RequestBody Map<String, String> request) {
        try {
            String accessToken = request.get("accessToken");
            String refreshToken = request.get("refreshToken");
            
            if ((accessToken == null || accessToken.isEmpty()) && 
                (refreshToken == null || refreshToken.isEmpty())) {
                return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                        .success(false)
                        .message("At least one token (access or refresh) is required")
                        .build());
            }
            
            jwtService.revokeToken(accessToken, refreshToken);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Tokens revoked successfully")
                    .data("Revoked specified tokens")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error revoking tokens: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to revoke tokens: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Süresi dolmuş token'ları temizler (Admin only)
     */
    @PostMapping("/cleanup-expired")
    @HasPermission(PermissionConstants.SYSTEM_CONFIG_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> cleanupExpiredTokens() {
        try {
            jwtService.markExpiredTokens();
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Expired tokens cleaned up successfully")
                    .data("Cleanup operation completed")
                    .build());
                    
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to cleanup expired tokens: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Yeni access token oluşturur (Admin only - test purposes)
     */
    @PostMapping("/generate-access/{userId}")
    @HasPermission(PermissionConstants.SYSTEM_CONFIG_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateAccessToken(@PathVariable Long userId) {
        try {
            // UserService'den Users entity'yi almak için yeni bir method gerekli
            log.info("Generating access token for user: {}", userId);
            
            Map<String, String> result = Map.of(
                "message", "Token generation - functionality to be implemented",
                "userId", userId.toString()
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                    .success(true)
                    .message("Access token generation - functionality to be implemented")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error generating access token for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, String>>builder()
                    .success(false)
                    .message("Failed to generate access token: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Yeni refresh token oluşturur (Admin only - test purposes)
     */
    @PostMapping("/generate-refresh/{userId}")
    @HasPermission(PermissionConstants.SYSTEM_CONFIG_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateRefreshToken(@PathVariable Long userId) {
        try {
            // UserService'den Users entity'yi almak için yeni bir method gerekli
            log.info("Generating refresh token for user: {}", userId);
            
            Map<String, String> result = Map.of(
                "message", "Token generation - functionality to be implemented",
                "userId", userId.toString()
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                    .success(true)
                    .message("Refresh token generation - functionality to be implemented")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error generating refresh token for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, String>>builder()
                    .success(false)
                    .message("Failed to generate refresh token: " + e.getMessage())
                    .build());
        }
    }
}