package com.prospect.crm.service;

import com.prospect.crm.dto.AuthRequestDto;
import com.prospect.crm.dto.AuthResponseDto;
import com.prospect.crm.dto.RefreshTokenRequestDto;
import com.prospect.crm.exception.AuthenticationException;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SystemLogService systemLogService;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      JwtService jwtService, SystemLogService systemLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.systemLogService = systemLogService;
    }
    
    /**
     * Kullanıcı girişi yapar ve token oluşturur
     */
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        try {
            // Kullanıcıyı bul
            Users user = userRepository.findByEmail(authRequestDto.getEmail())
                    .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
            
            // Şifreyi kontrol et
            if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
                systemLogService.logSecurity(
                    "Failed login attempt - invalid password",
                    "Email: " + authRequestDto.getEmail(),
                    user.getId().toString(),
                    null,
                    null
                );
                throw new AuthenticationException("Invalid email or password");
            }
            
            // Kullanıcı aktif mi kontrol et
            if (!user.getIsActive()) {
                systemLogService.logSecurity(
                    "Failed login attempt - inactive user",
                    "Email: " + authRequestDto.getEmail(),
                    user.getId().toString(),
                    null,
                    null
                );
                throw new AuthenticationException("Account is disabled");
            }
            
            // Eski tokenları iptal et
            jwtService.revokeAllUserTokens(user);
            
            // Yeni tokenları oluştur
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            
            // Başarılı giriş logu
            systemLogService.logSecurity(
                "User login successful",
                "Email: " + user.getEmail(),
                user.getId().toString(),
                null,
                null
            );
            
            // Response oluştur
            AuthResponseDto.UserInfoDto userInfo = new AuthResponseDto.UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                null, // firstName
                null  // lastName
            );
            
            return new AuthResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                7200L, // 2 saat
                "Login successful",
                userInfo
            );
            
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            systemLogService.logError(
                "Login failed",
                "Email: " + authRequestDto.getEmail() + ", Error: " + e.getMessage(),
                    Arrays.toString(e.getStackTrace()),
                "AuthService",
                "login"
            );
            throw new AuthenticationException("Login failed");
        }
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
                null, // firstName
                null  // lastName
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
                    Arrays.toString(e.getStackTrace()),
                "AuthService",
                "refreshToken"
            );
            throw new AuthenticationException("Token refresh failed");
        }
    }
    
    /**
     * Kullanıcı çıkışı yapar
     */
    public void logout(String accessToken, String refreshToken) {
        try {
            if (accessToken != null && jwtService.isAccessToken(accessToken)) {
                Long userId = jwtService.getUserIdFromToken(accessToken);
                Users user = userRepository.findById(userId).orElse(null);
                
                if (user != null) {
                    // Tüm tokenları iptal et
                    jwtService.revokeAllUserTokens(user);
                    
                    // Çıkış logu
                    systemLogService.logSecurity(
                        "User logout",
                        "User: " + user.getEmail(),
                        user.getId().toString(),
                        null,
                        null
                    );
                }
            } else if (refreshToken != null && jwtService.isRefreshToken(refreshToken)) {
                Long userId = jwtService.getUserIdFromToken(refreshToken);
                Users user = userRepository.findById(userId).orElse(null);
                
                if (user != null) {
                    // Tüm tokenları iptal et
                    jwtService.revokeAllUserTokens(user);
                    
                    // Çıkış logu
                    systemLogService.logSecurity(
                        "User logout via refresh token",
                        "User: " + user.getEmail(),
                        user.getId().toString(),
                        null,
                        null
                    );
                }
            }
            
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage(), e);
            systemLogService.logError(
                "Logout failed",
                "Error: " + e.getMessage(),
                e.getStackTrace().toString(),
                "AuthService",
                "logout"
            );
        }
    }
    
    /**
     * Token'ı doğrular ve kullanıcıyı döndürür
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
} 