package com.prospect.crm.security;

import com.prospect.crm.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Slf4j
@Component
public class PermissionEvaluator {
    
    private final PermissionService permissionService;
    
    public PermissionEvaluator(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    /**
     * Check if current user has the specified permission
     */
    public boolean hasPermission(String permissionKey) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            // Get user ID from authentication
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return false;
            }
            
            return permissionService.hasPermission(userId, permissionKey);
            
        } catch (Exception e) {
            log.error("Error checking permission {}: {}", permissionKey, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if current user has any of the specified permissions
     */
    public boolean hasAnyPermission(String... permissionKeys) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return false;
            }
            
            return Arrays.stream(permissionKeys)
                    .anyMatch(permissionKey -> permissionService.hasPermission(userId, permissionKey));
            
        } catch (Exception e) {
            log.error("Error checking permissions: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if current user has all of the specified permissions
     */
    public boolean hasAllPermissions(String... permissionKeys) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            Long userId = getUserIdFromAuthentication(authentication);
            if (userId == null) {
                return false;
            }
            
            return Arrays.stream(permissionKeys)
                    .allMatch(permissionKey -> permissionService.hasPermission(userId, permissionKey));
            
        } catch (Exception e) {
            log.error("Error checking permissions: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user ID from authentication
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        try {
            // Try to get user ID from JWT token claims
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User userDetails = 
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
                
                // Extract user ID from username (format: email:userId)
                String username = userDetails.getUsername();
                if (username != null && username.contains(":")) {
                    String[] parts = username.split(":");
                    if (parts.length == 2) {
                        return Long.parseLong(parts[1]);
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("Error extracting user ID from authentication: {}", e.getMessage());
            return null;
        }
    }
} 