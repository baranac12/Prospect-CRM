package com.prospect.crm.security;

import com.prospect.crm.config.JwtConfig;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.service.JwtService;
import com.prospect.crm.service.SubscriptionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    
    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService, 
                                 JwtConfig jwtConfig, SubscriptionService subscriptionService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String accessToken = getTokenFromCookie(request, jwtConfig.getAccessTokenCookieName());
            String refreshToken = getTokenFromCookie(request, jwtConfig.getRefreshTokenCookieName());
            
            if (accessToken == null) {
                accessToken = getTokenFromHeader(request);
            }
            
            if (accessToken != null && jwtService.isAccessToken(accessToken)) {
                try {
                    Long userId = jwtService.getUserIdFromToken(accessToken);
                    
                    if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserById(userId);
                        
                        if (userDetails != null) {
                            if (isProtectedEndpoint(request.getRequestURI()) && !subscriptionService.hasValidSubscription(userId)) {
                                sendSubscriptionRequiredResponse(response);
                                return;
                            }
                            
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                            );
                            
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Access token validation failed: {}", e.getMessage());
                    
                    if (refreshToken != null && jwtService.isRefreshToken(refreshToken)) {
                        try {
                            Long userId = jwtService.getUserIdFromToken(refreshToken);
                            
                            if (userId != null) {
                                Users user = userRepository.findById(userId).orElse(null);
                                
                                if (user != null) {
                                    String newAccessToken = jwtService.generateAccessToken(user);
                                    
                                    setCookie(response, jwtConfig.getAccessTokenCookieName(), newAccessToken, 
                                            (int) jwtConfig.getAccessTokenExpiration());
                                    
                                    UserDetails userDetails = userDetailsService.loadUserById(userId);
                                    
                                    if (userDetails != null) {
                                        if (isProtectedEndpoint(request.getRequestURI()) && !subscriptionService.hasValidSubscription(userId)) {
                                            sendSubscriptionRequiredResponse(response);
                                            return;
                                        }
                                        
                                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            userDetails.getAuthorities()
                                        );
                                        
                                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                        SecurityContextHolder.getContext().setAuthentication(authToken);
                                        
                                        log.info("Access token refreshed successfully for user: {}", user.getEmail());
                                    }
                                } else {
                                    log.warn("User not found for refresh token, clearing cookies");
                                    clearAuthCookies(response);
                                }
                            } else {
                                log.warn("Invalid refresh token, clearing cookies");
                                clearAuthCookies(response);
                            }
                        } catch (Exception refreshException) {
                            log.warn("Refresh token validation failed: {}", refreshException.getMessage());
                            clearAuthCookies(response);
                        }
                    } else {
                        clearAuthCookies(response);
                    }
                }
            } else if (refreshToken != null && jwtService.isRefreshToken(refreshToken)) {
                try {
                    Long userId = jwtService.getUserIdFromToken(refreshToken);
                    
                    if (userId != null) {
                        Users user = userRepository.findById(userId).orElse(null);
                        
                        if (user != null) {
                            String newAccessToken = jwtService.generateAccessToken(user);
                            
                            setCookie(response, jwtConfig.getAccessTokenCookieName(), newAccessToken, 
                                    (int) jwtConfig.getAccessTokenExpiration());
                            
                            UserDetails userDetails = userDetailsService.loadUserById(userId);
                            
                            if (userDetails != null) {
                                if (isProtectedEndpoint(request.getRequestURI()) && !subscriptionService.hasValidSubscription(userId)) {
                                    sendSubscriptionRequiredResponse(response);
                                    return;
                                }
                                
                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                                );
                                
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                
                                log.info("Access token generated from refresh token for user: {}", user.getEmail());
                            }
                        } else {
                            log.warn("User not found for refresh token, clearing cookies");
                            clearAuthCookies(response);
                        }
                    } else {
                        log.warn("Invalid refresh token, clearing cookies");
                        clearAuthCookies(response);
                    }
                } catch (Exception refreshException) {
                    log.warn("Refresh token validation failed: {}", refreshException.getMessage());
                    clearAuthCookies(response);
                }
            }
            
        } catch (Exception e) {
            log.error("JWT authentication filter error: {}", e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookieName.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }
    
    private String getTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
    
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(jwtConfig.getCookiePath());
        cookie.setDomain(jwtConfig.getCookieDomain());
        cookie.setMaxAge(maxAge);
        cookie.setSecure(jwtConfig.isCookieSecure());
        cookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(cookie);
    }
    
    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie(jwtConfig.getAccessTokenCookieName(), null);
        accessCookie.setPath(jwtConfig.getCookiePath());
        accessCookie.setDomain(jwtConfig.getCookieDomain());
        accessCookie.setMaxAge(0);
        accessCookie.setSecure(jwtConfig.isCookieSecure());
        accessCookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(accessCookie);
        
        Cookie refreshCookie = new Cookie(jwtConfig.getRefreshTokenCookieName(), null);
        refreshCookie.setPath(jwtConfig.getCookiePath());
        refreshCookie.setDomain(jwtConfig.getCookieDomain());
        refreshCookie.setMaxAge(0);
        refreshCookie.setSecure(jwtConfig.isCookieSecure());
        refreshCookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
        response.addCookie(refreshCookie);
    }

    private boolean isProtectedEndpoint(String requestUri) {
        return !requestUri.startsWith("/v1/admin") && 
               !requestUri.startsWith("/v1/logs") &&
               !requestUri.startsWith("/v1/auth") &&
               !requestUri.startsWith("/v1/users") &&
               !requestUri.startsWith("/v1/payments/success") &&
               !requestUri.startsWith("/v1/payments/cancel") &&
               !requestUri.startsWith("/v1/payments/webhook") &&
               !requestUri.startsWith("/v1/health") &&
               !requestUri.startsWith("/v1/test") &&
               !requestUri.startsWith("/error") &&
               !requestUri.startsWith("/swagger-ui") &&
               !requestUri.startsWith("/v3/api-docs") &&
               !requestUri.startsWith("/actuator");
    }

    private void sendSubscriptionRequiredResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.PAYMENT_REQUIRED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        String jsonResponse = "{\"error\":\"Subscription required\",\"message\":\"Please subscribe to access this feature\",\"code\":\"SUBSCRIPTION_REQUIRED\"}";
        response.getWriter().write(jsonResponse);
    }
} 