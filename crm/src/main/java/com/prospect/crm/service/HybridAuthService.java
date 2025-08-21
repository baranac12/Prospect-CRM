package com.prospect.crm.service;


import com.prospect.crm.dto.AuthRequestDto;
import com.prospect.crm.dto.AuthResponseDto;
import com.prospect.crm.dto.RefreshTokenRequestDto;
import com.prospect.crm.dto.OAuthLoginRequestDto;
import com.prospect.crm.dto.OAuthLoginResponseDto;
import com.prospect.crm.exception.AuthenticationException;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class HybridAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OAuthService oAuthService;
    private final SystemLogService systemLogService;

    public HybridAuthService(UserRepository userRepository, 
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           OAuthService oAuthService,
                           SystemLogService systemLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.oAuthService = oAuthService;
        this.systemLogService = systemLogService;
    }

    /**
     * Hybrid authentication - supports both OAuth and username/password
     */
    public AuthResponseDto authenticate(AuthRequestDto authRequestDto) {
        try {
            // Check if OAuth provider is specified
            if (authRequestDto.getOauthProvider() != null && !authRequestDto.getOauthProvider().trim().isEmpty()) {
                return authenticateWithOAuth(authRequestDto);
            } else {
                return authenticateWithPassword(authRequestDto);
            }
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage(), e);
            systemLogService.logError("Authentication failed", 
                "Email: " + authRequestDto.getEmail() + ", Error: " + e.getMessage(),
                e.getStackTrace().toString(), "HybridAuthService", "authenticate");
            throw new AuthenticationException("Authentication failed");
        }
    }

    /**
     * Authenticate with OAuth provider
     */
    private AuthResponseDto authenticateWithOAuth(AuthRequestDto authRequestDto) {
        try {
            // Create OAuth login request
            OAuthLoginRequestDto oauthRequest = OAuthLoginRequestDto.builder()
                    .email(authRequestDto.getEmail())
                    .provider(authRequestDto.getOauthProvider())
                    .redirectUri("http://localhost:8080/v1/oauth/callback/login")
                    .build();

            // Initiate OAuth login
            OAuthLoginResponseDto oauthResponse = oAuthService.initiateOAuthLogin(oauthRequest);

            if ("LOGIN".equals(oauthResponse.getAction())) {
                // User exists, proceed with OAuth authentication
                return authenticateUserWithOAuth(authRequestDto.getEmail(), authRequestDto.getOauthProvider());
            } else {
                // User doesn't exist, need to register
                throw new AuthenticationException("User not found. Please register first via OAuth.");
            }

        } catch (Exception e) {
            log.error("OAuth authentication error: {}", e.getMessage(), e);
            throw new AuthenticationException("OAuth authentication failed: " + e.getMessage());
        }
    }

    /**
     * Authenticate with username/password (which will use OAuth internally)
     */
    private AuthResponseDto authenticateWithPassword(AuthRequestDto authRequestDto) {
        try {
            // Find user by email
            Optional<Users> userOpt = userRepository.findByEmail(authRequestDto.getEmail());
            
            if (userOpt.isEmpty()) {
                systemLogService.logSecurity("Failed login attempt - user not found",
                    "Email: " + authRequestDto.getEmail(), null, null, null);
                throw new AuthenticationException("Invalid email or password");
            }

            Users user = userOpt.get();

            // Check if user has password set
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                throw new AuthenticationException("This account requires OAuth authentication. Please use OAuth login.");
            }

            // Verify password
            if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
                systemLogService.logSecurity("Failed login attempt - invalid password",
                    "Email: " + authRequestDto.getEmail(), user.getId().toString(), null, null);
                throw new AuthenticationException("Invalid email or password");
            }

            // Check if user is active
            if (!user.getIsActive()) {
                systemLogService.logSecurity("Failed login attempt - inactive user",
                    "Email: " + authRequestDto.getEmail(), user.getId().toString(), null, null);
                throw new AuthenticationException("Account is disabled");
            }

            // Use traditional authentication for password-based login
            return createAuthResponse(user, "Traditional authentication");

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Password authentication error: {}", e.getMessage(), e);
            throw new AuthenticationException("Authentication failed");
        }
    }

    /**
     * Authenticate user with OAuth (internal method)
     */
    private AuthResponseDto authenticateUserWithOAuth(String email, String provider) {
        try {
            // Find user
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            // Check if user is active
            if (!user.getIsActive()) {
                throw new AuthenticationException("Account is disabled");
            }

            // Check if user has valid OAuth token for the provider
            if (!hasValidOAuthToken(user.getId(), provider)) {
                throw new AuthenticationException("OAuth authentication required. Please reconnect your " + provider + " account.");
            }

            // Revoke old tokens
            jwtService.revokeAllUserTokens(user);

            // Generate new tokens
            jwtService.generateAccessToken(user);
            jwtService.generateRefreshToken(user);

            // Log successful OAuth authentication
            systemLogService.logSecurity("OAuth authentication successful",
                "Provider: " + provider + ", Email: " + email, user.getId().toString(), null, null);

            return createAuthResponse(user, "OAuth authentication via " + provider);

        } catch (Exception e) {
            log.error("OAuth authentication error: {}", e.getMessage(), e);
            throw new AuthenticationException("OAuth authentication failed: " + e.getMessage());
        }
    }

    /**
     * Check if user has OAuth accounts connected
     */
    private boolean userHasOAuthAccounts(Long userId) {
        try {
            return !oAuthService.getConnectedAccounts(userId).isEmpty();
        } catch (Exception e) {
            log.warn("Error checking OAuth accounts for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Get primary OAuth provider for user
     */
    private String getPrimaryOAuthProvider(Long userId) {
        try {
            var accounts = oAuthService.getConnectedAccounts(userId);
            if (!accounts.isEmpty()) {
                return accounts.get(0).getProvider();
            }
            return "google"; // Default to Google
        } catch (Exception e) {
            log.warn("Error getting primary OAuth provider for user {}: {}", userId, e.getMessage());
            return "google";
        }
    }

    /**
     * Check if user has valid OAuth token for provider
     */
    private boolean hasValidOAuthToken(Long userId, String provider) {
        try {
            var accounts = oAuthService.getConnectedAccounts(userId);
            return accounts.stream()
                    .anyMatch(account -> provider.equals(account.getProvider()) && "CONNECTED".equals(account.getStatus()));
        } catch (Exception e) {
            log.warn("Error checking OAuth token validity for user {} and provider {}: {}", userId, provider, e.getMessage());
            return false;
        }
    }

    /**
     * Create authentication response
     */
    private AuthResponseDto createAuthResponse(Users user, String authMethod) {
        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Create user info
        AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getName(),
            user.getSurname()
        );

        return new AuthResponseDto(
            accessToken,
            refreshToken,
            "Bearer",
            7200L, // 2 hours
            "Authentication successful via " + authMethod,
            userInfo
        );
    }

    /**
     * Refresh token ile yeni access token oluşturur
     */
    public AuthResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        try {
            // Refresh token'ı doğrula
            if (!jwtService.isRefreshToken(refreshTokenRequestDto.getRefreshToken())) {
                throw new AuthenticationException("Invalid refresh token");
            }

            // Kullanıcı ID'sini al
            Long userId = jwtService.getUserIdFromToken(refreshTokenRequestDto.getRefreshToken());

            // Kullanıcıyı bul
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            // Kullanıcı aktif mi kontrol et
            if (!user.getIsActive()) {
                throw new AuthenticationException("Account is disabled");
            }

            // Eski tokenları iptal et
            jwtService.revokeAllUserTokens(user);

            // Yeni tokenları oluştur
            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            // Token yenileme logu
            systemLogService.logSecurity(
                "Token refreshed",
                "User: " + user.getEmail(),
                user.getId().toString(),
                null,
                null
            );

            // Response oluştur
            AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getSurname()
            );

            return new AuthResponseDto(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                7200L, // 2 saat
                "Token refreshed successfully",
                userInfo
            );

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage(), e);
            systemLogService.logError(
                "Token refresh failed",
                "Refresh token: " + refreshTokenRequestDto.getRefreshToken().substring(0, Math.min(refreshTokenRequestDto.getRefreshToken().length(), 20)) + "..., Error: " + e.getMessage(),
                e.getStackTrace().toString(),
                "HybridAuthService",
                "refreshToken"
            );
            throw new AuthenticationException("Token refresh failed");
        }
    }

    /**
     * Validate token and get user
     */
    public Users validateTokenAndGetUser(String token) {
        try {
            if (!jwtService.isAccessToken(token)) {
                throw new AuthenticationException("Invalid access token");
            }

            Long userId = jwtService.getUserIdFromToken(token);
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            if (!user.getIsActive()) {
                throw new AuthenticationException("Account is disabled");
            }

            return user;

        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage(), e);
            throw new AuthenticationException("Token validation failed");
        }
    }

    /**
     * Logout user
     */
    public void logout(String accessToken, String refreshToken) {
        try {
            if (accessToken != null && jwtService.isAccessToken(accessToken)) {
                Long userId = jwtService.getUserIdFromToken(accessToken);
                Users user = userRepository.findById(userId).orElse(null);

                if (user != null) {
                    jwtService.revokeAllUserTokens(user);
                    systemLogService.logSecurity("User logout",
                        "User: " + user.getEmail(), user.getId().toString(), null, null);
                }
            } else if (refreshToken != null && jwtService.isRefreshToken(refreshToken)) {
                Long userId = jwtService.getUserIdFromToken(refreshToken);
                Users user = userRepository.findById(userId).orElse(null);

                if (user != null) {
                    jwtService.revokeAllUserTokens(user);
                    systemLogService.logSecurity("User logout via refresh token",
                        "User: " + user.getEmail(), user.getId().toString(), null, null);
                }
            }

        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage(), e);
            systemLogService.logError("Logout failed", "Error: " + e.getMessage(),
                e.getStackTrace().toString(), "HybridAuthService", "logout");
        }
    }
} 