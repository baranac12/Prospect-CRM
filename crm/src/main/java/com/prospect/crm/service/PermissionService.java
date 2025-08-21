package com.prospect.crm.service;

import com.prospect.crm.model.Users;
import com.prospect.crm.repository.RolePermissionRepository;
import com.prospect.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionService {
    
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRepository userRepository;
    
    public PermissionService(RolePermissionRepository rolePermissionRepository, UserRepository userRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Kullanıcının belirli bir izne sahip olup olmadığını kontrol eder
     */
    public boolean hasPermission(Long userId, String permissionKey) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (user.getRoleId() == null) {
                return false;
            }
            
            return rolePermissionRepository.existsByRoleIdIdAndPermissionKey(
                user.getRoleId().getId(), permissionKey);
            
        } catch (Exception e) {
            log.error("Error checking permission for user {} and permission {}: {}", 
                userId, permissionKey, e.getMessage());
            return false;
        }
    }
    
    /**
     * Kullanıcının tüm izinlerini getirir
     */
    public Set<String> getUserPermissions(Long userId) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (user.getRoleId() == null) {
                return Set.of();
            }
            
            return rolePermissionRepository.findPermissionKeysByRoleId(user.getRoleId().getId())
                    .stream()
                    .collect(Collectors.toSet());
            
        } catch (Exception e) {
            log.error("Error getting permissions for user {}: {}", userId, e.getMessage());
            return Set.of();
        }
    }
} 