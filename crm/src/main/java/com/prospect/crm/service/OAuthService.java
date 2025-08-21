package com.prospect.crm.service;

import com.prospect.crm.config.OAuthConfig;
import com.prospect.crm.constant.OAuthProvider;
import com.prospect.crm.dto.OAuthResponseDto;
import com.prospect.crm.dto.OAuthUserInfoDto;
import com.prospect.crm.dto.OAuthLoginRequestDto;
import com.prospect.crm.dto.OAuthLoginResponseDto;
import com.prospect.crm.model.OauthToken;
import com.prospect.crm.model.Users;
import com.prospect.crm.model.Role;
import com.prospect.crm.model.SubscriptionType;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.repository.OauthTokenRepository;
import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.repository.RoleRepository;
import com.prospect.crm.repository.SubscriptionTypeRepository;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OAuthService {

    private final OAuthConfig oAuthConfig;
    private final OauthTokenRepository oauthTokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubscriptionTypeRepository subscriptionTypeRepository;
    private final UserSubsInfoRepository userSubsInfoRepository;
    private final SystemLogService systemLogService;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    public OAuthService(OAuthConfig oAuthConfig, 
                       OauthTokenRepository oauthTokenRepository,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       SubscriptionTypeRepository subscriptionTypeRepository,
                       UserSubsInfoRepository userSubsInfoRepository,
                       SystemLogService systemLogService,
                       PasswordEncoder passwordEncoder) {
        this.oAuthConfig = oAuthConfig;
        this.oauthTokenRepository = oauthTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
        this.userSubsInfoRepository = userSubsInfoRepository;
        this.systemLogService = systemLogService;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Initiates OAuth login/register process
     */
    public OAuthLoginResponseDto initiateOAuthLogin(OAuthLoginRequestDto request) {
        try {
            // First check if user exists by email
            Optional<Users> existingUser = userRepository.findByEmail(request.getEmail());
            
            if (existingUser.isPresent()) {
                // User exists - LOGIN
                return OAuthLoginResponseDto.builder()
                        .action("LOGIN")
                        .message("User found, logging in with OAuth")
                        .userInfo(OAuthUserInfoDto.builder()
                                .email(request.getEmail())
                                .name(existingUser.get().getName())
                                .surname(existingUser.get().getSurname())
                                .provider(request.getProvider())
                                .build())
                        .requiresAdditionalInfo(false)
                        .build();
            } else {
                // User doesn't exist - REGISTER
                String state = UUID.randomUUID().toString();
                String authUrl = createAuthorizationUrl(request.getProvider(), request.getRedirectUri(), state);
                
                return OAuthLoginResponseDto.builder()
                        .action("REGISTER")
                        .message("OAuth authorization required for new user registration")
                        .authorizationUrl(authUrl)
                        .state(state)
                        .requiresAdditionalInfo(true)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error initiating OAuth login: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initiate OAuth login", e);
        }
    }

    /**
     * Handles OAuth callback and logs in/registers user
     */
    public OAuthLoginResponseDto handleOAuthCallback(String provider, String code, String redirectUri, 
                                                    String password, String phone) {
        try {
            // Token exchange
            Map<String, Object> tokenResponse = exchangeCodeForTokens(provider, code, redirectUri);
            
            // User info al
            Map<String, Object> userInfoMap = getUserInfo(provider, (String) tokenResponse.get("access_token"));
            
            // User info'yu DTO'ya çevir
            OAuthUserInfoDto userInfo = mapToUserInfoDto(provider, userInfoMap);
            
            // Check if user exists
            Optional<Users> existingUser = userRepository.findByEmail(userInfo.getEmail());
            
            if (existingUser.isPresent()) {
                // Login - Save OAuth token
                Users user = existingUser.get();
                saveOAuthToken(user.getId(), provider, userInfoMap, tokenResponse);
                
                systemLogService.logSecurity("OAuth login successful", 
                    "Provider: " + provider + ", User: " + user.getId() + ", Email: " + userInfo.getEmail(),
                    user.getId().toString(), null, null);
                
                return OAuthLoginResponseDto.builder()
                        .action("LOGIN")
                        .message("OAuth login successful")
                        .userInfo(userInfo)
                        .requiresAdditionalInfo(false)
                        .build();
                        
            } else {
                // Register - Create new user
                if (password == null || password.trim().isEmpty()) {
                    throw new RuntimeException("Password required for new user");
                }
                
                Users newUser = createUserFromOAuth(userInfo, password, phone);
                saveOAuthToken(newUser.getId(), provider, userInfoMap, tokenResponse);
                
                systemLogService.logSecurity("OAuth registration successful", 
                    "Provider: " + provider + ", User: " + newUser.getId() + ", Email: " + userInfo.getEmail(),
                    newUser.getId().toString(), null, null);
                
                return OAuthLoginResponseDto.builder()
                        .action("REGISTER")
                        .message("OAuth registration successful")
                        .userInfo(userInfo)
                        .requiresAdditionalInfo(false)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error handling OAuth callback: {}", e.getMessage(), e);
            systemLogService.logError("OAuth callback failed", 
                "Provider: " + provider + ", Error: " + e.getMessage(), 
                e.getMessage(), "OAuthService", "handleOAuthCallback");
            throw new RuntimeException("Failed to handle OAuth callback", e);
        }
    }

    /**
     * OAuth authorization URL oluşturur
     */
    public String createAuthorizationUrl(String provider, String redirectUri, String state) {
        try {
            OAuthProvider oAuthProvider = OAuthProvider.fromCode(provider);
            String clientId = getClientId(provider);
            String scope = getScope(provider);
            
            String authUrl = switch (oAuthProvider) {
                case GOOGLE -> oAuthConfig.getGoogle().getAuthorizationUri();
                case MICROSOFT -> oAuthConfig.getMicrosoft().getAuthorizationUri();
            };
            
            String url = authUrl + "?" +
                    "client_id=" + clientId +
                    "&redirect_uri=" + redirectUri +
                    "&scope=" + scope +
                    "&response_type=code" +
                    "&state=" + state +
                    "&access_type=offline";
            
            log.info("OAuth authorization URL created for provider: {}", provider);
            return url;
            
        } catch (Exception e) {
            log.error("Error creating OAuth authorization URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create OAuth authorization URL", e);
        }
    }

    /**
     * OAuth callback'i işler ve token'ları alır
     */
    public OAuthResponseDto handleCallback(String provider, String code, String redirectUri, Long userId) {
        try {
            // Token exchange
            Map<String, Object> tokenResponse = exchangeCodeForTokens(provider, code, redirectUri);
            
            // User info al
            Map<String, Object> userInfo = getUserInfo(provider, (String) tokenResponse.get("access_token"));
            
            // Token'ı veritabanına kaydet
            OauthToken oauthToken = saveToken(userId, provider, userInfo, tokenResponse);
            
            // Response oluştur
            OAuthResponseDto response = OAuthResponseDto.builder()
                    .provider(provider)
                    .email((String) userInfo.get("email"))
                    .status("CONNECTED")
                    .connectedAt(oauthToken.getCreatedAt())
                    .expiresAt(oauthToken.getExpiresAt())
                    .scopes(List.of(oauthToken.getScope().split(",")))
                    .accessToken(oauthToken.getAccessToken())
                    .refreshToken(oauthToken.getRefreshToken())
                    .build();
            
            systemLogService.logSecurity("OAuth connection successful", 
                "Provider: " + provider + ", User: " + userId + ", Email: " + response.getEmail(),
                userId.toString(), null, null);
            
            return response;
            
        } catch (Exception e) {
            log.error("Error handling OAuth callback: {}", e.getMessage(), e);
            systemLogService.logError("OAuth callback failed", "Provider: " + provider + ", User: " + userId, e.getMessage(), "OAuthService", "handleCallback");
            throw new RuntimeException("Failed to handle OAuth callback", e);
        }
    }

    /**
     * Gets user's connected email accounts
     */
    public List<OAuthResponseDto> getConnectedAccounts(Long userId) {
        try {
            List<OauthToken> tokens = oauthTokenRepository.findActiveTokensByUser(userId);
            
            return tokens.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("Error getting connected accounts for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to get connected accounts", e);
        }
    }

    /**
     * Email hesabını bağlantısını keser
     */
    public void disconnectAccount(Long userId, String provider, String email) {
        try {
            Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));
            Optional<OauthToken> tokenOpt = oauthTokenRepository.findByUserIdAndProviderAndEmail(user, provider, email);
            
            if (tokenOpt.isPresent()) {
                OauthToken token = tokenOpt.get();
                token.setRevoked(true);
                token.setUpdatedAt(LocalDateTime.now());
                oauthTokenRepository.save(token);
                
                systemLogService.logSecurity("OAuth account disconnected", 
                    "Provider: " + provider + ", User: " + userId + ", Email: " + email,
                    userId.toString(), null, null);
            }
            
        } catch (Exception e) {
            log.error("Error disconnecting account: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to disconnect account", e);
        }
    }

    /**
     * Token'ı yeniler
     */
    public OauthToken refreshToken(OauthToken token) {
        try {
            Map<String, Object> tokenResponse = refreshAccessToken(token.getProvider(), token.getRefreshToken());
            
            token.setAccessToken((String) tokenResponse.get("access_token"));
            token.setExpiresAt(LocalDateTime.now().plusSeconds((Integer) tokenResponse.get("expires_in")));
            token.setUpdatedAt(LocalDateTime.now());
            
            OauthToken refreshedToken = oauthTokenRepository.save(token);
            
            log.info("Token refreshed for user: {}, provider: {}", token.getUserId().getId(), token.getProvider());
            return refreshedToken;
            
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            token.setExpired(true);
            oauthTokenRepository.save(token);
            throw new RuntimeException("Failed to refresh token", e);
        }
    }

    /**
     * Geçerli token'ı getirir (gerekirse yeniler)
     */
    public Optional<OauthToken> getValidToken(Long userId, String provider, String email) {
        try {

            Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));
            Optional<OauthToken> tokenOpt = oauthTokenRepository.findByUserIdAndProviderAndEmail(user, provider, email);
            
            if (tokenOpt.isPresent()) {
                OauthToken token = tokenOpt.get();
                
                // Token süresi dolmuşsa yenile
                if (token.getExpiresAt().minusSeconds(oAuthConfig.getToken().getRefreshThreshold()).isBefore(LocalDateTime.now())) {
                    token = refreshToken(token);
                }
                
                return Optional.of(token);
            }
            
            return Optional.empty();
            
        } catch (Exception e) {
            log.error("Error getting valid token: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    // Private helper methods

    private String getClientId(String provider) {
        // Bu değerler application.properties'den alınmalı
        return switch (provider.toLowerCase()) {
            case "google" -> "your-google-client-id";
            case "microsoft" -> "your-microsoft-client-id";
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    private String getClientSecret(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> "your-google-client-secret";
            case "microsoft" -> "your-microsoft-client-secret";
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    private String getScope(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> "openid profile email https://www.googleapis.com/auth/gmail.send https://www.googleapis.com/auth/gmail.readonly https://www.googleapis.com/auth/gmail.modify https://www.googleapis.com/auth/gmail.labels";
            case "microsoft" -> "openid profile email offline_access https://graph.microsoft.com/Mail.Send https://graph.microsoft.com/Mail.Read https://graph.microsoft.com/Mail.ReadWrite https://graph.microsoft.com/Mail.ReadWrite.Shared";
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    private Map<String, Object> exchangeCodeForTokens(String provider, String code, String redirectUri) {
        String tokenUri = switch (OAuthProvider.fromCode(provider)) {
            case GOOGLE -> oAuthConfig.getGoogle().getTokenUri();
            case MICROSOFT -> oAuthConfig.getMicrosoft().getTokenUri();
        };
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", getClientId(provider));
        body.add("client_secret", getClientSecret(provider));
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {});
        return response.getBody();
    }

    private Map<String, Object> getUserInfo(String provider, String accessToken) {
        String userInfoUri = switch (OAuthProvider.fromCode(provider)) {
            case GOOGLE -> oAuthConfig.getGoogle().getUserInfoUri();
            case MICROSOFT -> oAuthConfig.getMicrosoft().getUserInfoUri();
        };
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, Object>>() {});
        return response.getBody();
    }

    private Map<String, Object> refreshAccessToken(String provider, String refreshToken) {
        String tokenUri = switch (OAuthProvider.fromCode(provider)) {
            case GOOGLE -> oAuthConfig.getGoogle().getTokenUri();
            case MICROSOFT -> oAuthConfig.getMicrosoft().getTokenUri();
        };
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", getClientId(provider));
        body.add("client_secret", getClientSecret(provider));
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, new ParameterizedTypeReference<Map<String, Object>>() {});
        return response.getBody();
    }

    private OauthToken saveToken(Long userId, String provider, Map<String, Object> userInfo, Map<String, Object> tokenResponse) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        OauthToken token = new OauthToken();
        token.setUserId(user);
        token.setProvider(provider);
        token.setEmail((String) userInfo.get("email"));
        token.setAccessToken((String) tokenResponse.get("access_token"));
        token.setRefreshToken((String) tokenResponse.get("refresh_token"));
        token.setTokenType((String) tokenResponse.get("token_type"));
        token.setScope(getScope(provider));
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusSeconds((Integer) tokenResponse.get("expires_in")));
        token.setRevoked(false);
        token.setExpired(false);
        token.setCreatedAt(LocalDateTime.now());
        token.setUpdatedAt(LocalDateTime.now());
        
        return oauthTokenRepository.save(token);
    }

    private OAuthResponseDto mapToResponseDto(OauthToken token) {
        return OAuthResponseDto.builder()
                .provider(token.getProvider())
                .email(token.getEmail())
                .status(token.getExpired() ? "EXPIRED" : "CONNECTED")
                .connectedAt(token.getCreatedAt())
                .expiresAt(token.getExpiresAt())
                .scopes(List.of(token.getScope().split(" ")))
                .build();
    }

    /**
     * OAuth user info'yu DTO'ya çevirir
     */
    private OAuthUserInfoDto mapToUserInfoDto(String provider, Map<String, Object> userInfoMap) {
        String email = (String) userInfoMap.get("email");
        String name = "";
        String surname = "";
        
        if ("google".equals(provider)) {
            name = (String) userInfoMap.get("given_name");
            surname = (String) userInfoMap.get("family_name");
        } else if ("microsoft".equals(provider)) {
            String displayName = (String) userInfoMap.get("displayName");
            if (displayName != null && !displayName.isEmpty()) {
                String[] nameParts = displayName.split(" ", 2);
                name = nameParts[0];
                surname = nameParts.length > 1 ? nameParts[1] : "";
            }
        }
        
        return OAuthUserInfoDto.builder()
                .provider(provider)
                .email(email)
                .name(name)
                .surname(surname)
                .givenName((String) userInfoMap.get("given_name"))
                .familyName((String) userInfoMap.get("family_name"))
                .displayName((String) userInfoMap.get("displayName"))
                .picture((String) userInfoMap.get("picture"))
                .locale((String) userInfoMap.get("locale"))
                .sub((String) userInfoMap.get("sub"))
                .build();
    }

    /**
     * OAuth token'ı kaydeder
     */
    private OauthToken saveOAuthToken(Long userId, String provider, Map<String, Object> userInfo, Map<String, Object> tokenResponse) {
        return saveToken(userId, provider, userInfo, tokenResponse);
    }

    /**
     * OAuth bilgilerinden yeni kullanıcı oluşturur
     */
    private Users createUserFromOAuth(OAuthUserInfoDto userInfo, String password, String phone) {
        // Default USER rolünü al
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        
        // Username oluştur (email'den)
        String username = userInfo.getEmail().split("@")[0];
        
        // Username benzersiz mi kontrol et
        int counter = 1;
        String originalUsername = username;
        while (userRepository.findByUsername(username).isPresent()) {
            username = originalUsername + counter;
            counter++;
        }
        
        Users newUser = new Users();
        newUser.setName(userInfo.getName());
        newUser.setSurname(userInfo.getSurname());
        newUser.setEmail(userInfo.getEmail());
        newUser.setPhone(phone);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRoleId(defaultRole);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        
        Users savedUser = userRepository.save(newUser);
        
        // 3 günlük trial subscription oluştur
        createTrialSubscription(savedUser);
        
        return savedUser;
    }

    /**
     * Trial subscription oluşturur
     */
    private void createTrialSubscription(Users user) {
        try {
            // Trial subscription type'ını bul
            SubscriptionType trialType = subscriptionTypeRepository.findByCodeAndIsActiveTrue("trial")
                    .orElseThrow(() -> new RuntimeException("Trial subscription type not found"));
            
            UserSubsInfo subscription = new UserSubsInfo();
            subscription.setUsersId(user);
            subscription.setSubscriptionTypeId(trialType);
            subscription.setSubsStartDate(LocalDateTime.now());
            subscription.setSubsEndDate(LocalDateTime.now().plusDays(3));
            subscription.setPaymentCheck(true);
            subscription.setIsActive(true);
            subscription.setCreatedAt(LocalDateTime.now());
            subscription.setUpdatedAt(LocalDateTime.now());
            
            userSubsInfoRepository.save(subscription);
            
        } catch (Exception e) {
            log.error("Error creating trial subscription for user {}: {}", user.getId(), e.getMessage());
            // Trial subscription oluşturulamazsa kullanıcı kaydını engelleme
        }
    }
} 