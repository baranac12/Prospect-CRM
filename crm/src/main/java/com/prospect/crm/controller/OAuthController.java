package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.OAuthRequestDto;
import com.prospect.crm.dto.OAuthResponseDto;
import com.prospect.crm.dto.OAuthLoginRequestDto;
import com.prospect.crm.dto.OAuthLoginResponseDto;
import com.prospect.crm.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    /**
     * OAuth login/register işlemini başlatır
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<OAuthLoginResponseDto>> initiateOAuthLogin(
            @RequestBody OAuthLoginRequestDto request) {
        
        try {
            OAuthLoginResponseDto response = oAuthService.initiateOAuthLogin(request);
            
            return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
            
        } catch (Exception e) {
            log.error("Error initiating OAuth login: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to initiate OAuth login", "OAUTH_007", e.getMessage()));
        }
    }

    /**
     * OAuth callback'i işler ve kullanıcıyı login/register eder
     */
    @PostMapping("/callback/login")
    public ResponseEntity<ApiResponse<OAuthLoginResponseDto>> handleOAuthLoginCallback(
            @RequestParam String provider,
            @RequestParam String code,
            @RequestParam String redirectUri,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String phone) {
        
        try {
            OAuthLoginResponseDto response = oAuthService.handleOAuthCallback(provider, code, redirectUri, password, phone);
            
            return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
            
        } catch (Exception e) {
            log.error("Error handling OAuth login callback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to handle OAuth login callback", "OAUTH_008", e.getMessage()));
        }
    }

    /**
     * OAuth authorization URL oluşturur
     */
    @PostMapping("/authorize")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> createAuthorizationUrl(
            @RequestBody OAuthRequestDto request,
            @RequestParam Long userId) {
        
        try {
            String state = UUID.randomUUID().toString();
            String redirectUri = request.getRedirectUri() != null ? 
                request.getRedirectUri() : 
                "http://localhost:8080/v1/oauth/callback/" + request.getProvider();
            
            String authUrl = oAuthService.createAuthorizationUrl(request.getProvider(), redirectUri, state);
            
            Map<String, String> response = Map.of(
                "authorizationUrl", authUrl,
                "state", state,
                "provider", request.getProvider()
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "Authorization URL created successfully"));
            
        } catch (Exception e) {
            log.error("Error creating authorization URL: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to create authorization URL", "OAUTH_001", e.getMessage()));
        }
    }

    /**
     * OAuth callback'i işler
     */
    @GetMapping("/callback/{provider}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OAuthResponseDto>> handleCallback(
            @PathVariable String provider,
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam Long userId) {
        
        try {
            String redirectUri = "http://localhost:8080/v1/oauth/callback/" + provider;
            
            OAuthResponseDto response = oAuthService.handleCallback(provider, code, redirectUri, userId);
            
            return ResponseEntity.ok(ApiResponse.success(response, "OAuth connection successful"));
            
        } catch (Exception e) {
            log.error("Error handling OAuth callback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to handle OAuth callback", "OAUTH_002", e.getMessage()));
        }
    }

    /**
     * Kullanıcının bağlı email hesaplarını getirir
     */
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<OAuthResponseDto>>> getConnectedAccounts(@RequestParam Long userId) {
        try {
            List<OAuthResponseDto> accounts = oAuthService.getConnectedAccounts(userId);
            
            return ResponseEntity.ok(ApiResponse.success(accounts, "Connected accounts retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting connected accounts: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get connected accounts", "OAUTH_003", e.getMessage()));
        }
    }

    /**
     * Email hesabını bağlantısını keser
     */
    @DeleteMapping("/disconnect")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<String>> disconnectAccount(
            @RequestParam Long userId,
            @RequestParam String provider,
            @RequestParam String email) {
        
        try {
            oAuthService.disconnectAccount(userId, provider, email);
            
            return ResponseEntity.ok(ApiResponse.success("Account disconnected successfully"));
            
        } catch (Exception e) {
            log.error("Error disconnecting account: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to disconnect account", "OAUTH_004", e.getMessage()));
        }
    }

    /**
     * OAuth provider'larını listeler
     */
    @GetMapping("/providers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getProviders() {
        try {
            List<Map<String, String>> providers = List.of(
                Map.of("code", "google", "name", "Gmail", "description", "Google Gmail OAuth2"),
                Map.of("code", "microsoft", "name", "Outlook", "description", "Microsoft Outlook OAuth2")
            );
            
            return ResponseEntity.ok(ApiResponse.success(providers, "OAuth providers retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting OAuth providers: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get OAuth providers", "OAUTH_005", e.getMessage()));
        }
    }

    /**
     * OAuth durumunu kontrol eder
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOAuthStatus(@RequestParam Long userId) {
        try {
            List<OAuthResponseDto> accounts = oAuthService.getConnectedAccounts(userId);
            
            Map<String, Object> status = Map.of(
                "userId", userId,
                "connectedAccounts", accounts.size(),
                "accounts", accounts,
                "hasGmail", accounts.stream().anyMatch(acc -> "google".equals(acc.getProvider())),
                "hasOutlook", accounts.stream().anyMatch(acc -> "microsoft".equals(acc.getProvider()))
            );
            
            return ResponseEntity.ok(ApiResponse.success(status, "OAuth status retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting OAuth status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get OAuth status", "OAUTH_006", e.getMessage()));
        }
    }
} 