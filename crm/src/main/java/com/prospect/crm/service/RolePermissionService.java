package com.prospect.crm.service;

import com.prospect.crm.model.RolePermission;
import com.prospect.crm.repository.RolePermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class RolePermissionService {
    
    private final RolePermissionRepository rolePermissionRepository;
    
    public RolePermissionService(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }
    
    /**
     * Role'a izin ekler
     */
    public RolePermission addPermissionToRole(Long roleId, String permissionKey) {
        try {
            // İzin zaten var mı kontrol et
            if (rolePermissionRepository.existsByRoleIdIdAndPermissionKey(roleId, permissionKey)) {
                log.warn("Permission {} already exists for role {}", permissionKey, roleId);
                return null;
            }
            
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(new com.prospect.crm.model.Role());
            rolePermission.getRoleId().setId(roleId);
            rolePermission.setPermissionKey(permissionKey);
            rolePermission.setGrantedAt(LocalDateTime.now());
            
            RolePermission saved = rolePermissionRepository.save(rolePermission);
            
            log.info("Permission {} added to role {}", permissionKey, roleId);
            return saved;
            
        } catch (Exception e) {
            log.error("Error adding permission {} to role {}: {}", permissionKey, roleId, e.getMessage());
            throw new RuntimeException("Failed to add permission to role", e);
        }
    }
    
    /**
     * Role'dan izin kaldırır
     */
    public void removePermissionFromRole(Long roleId, String permissionKey) {
        try {
            List<RolePermission> permissions = rolePermissionRepository.findByRoleIdId(roleId);
            permissions.stream()
                    .filter(rp -> permissionKey.equals(rp.getPermissionKey()))
                    .findFirst()
                    .ifPresent(rolePermissionRepository::delete);
            
            log.info("Permission {} removed from role {}", permissionKey, roleId);
                    
        } catch (Exception e) {
            log.error("Error removing permission {} from role {}: {}", permissionKey, roleId, e.getMessage());
            throw new RuntimeException("Failed to remove permission from role", e);
        }
    }
    
    /**
     * Role'ın tüm izinlerini getirir
     */
    public List<RolePermission> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findByRoleIdId(roleId);
    }
    
    /**
     * Role'ın tüm izinlerini siler
     */
    public void clearRolePermissions(Long roleId) {
        rolePermissionRepository.deleteByRoleIdId(roleId);
        log.info("All permissions cleared for role {}", roleId);
    }
    
    /**
     * Role'ın permission key'lerini getirir
     */
    public Set<String> getRolePermissionKeys(Long roleId) {
        return rolePermissionRepository.findPermissionKeysByRoleId(roleId)
                .stream()
                .collect(Collectors.toSet());
    }
    
    /**
     * Birden fazla role'a aynı anda izin ekler
     */
    public void addPermissionToRoles(String permissionKey, List<Long> roleIds) {
        roleIds.forEach(roleId -> addPermissionToRole(roleId, permissionKey));
    }
    
    /**
     * Birden fazla role'dan aynı anda izin kaldırır
     */
    public void removePermissionFromRoles(String permissionKey, List<Long> roleIds) {
        roleIds.forEach(roleId -> removePermissionFromRole(roleId, permissionKey));
    }
    
    /**
     * Role'a birden fazla izin ekler
     */
    public void addPermissionsToRole(Long roleId, List<String> permissionKeys) {
        permissionKeys.forEach(permissionKey -> addPermissionToRole(roleId, permissionKey));
    }
    
    /**
     * Role'dan birden fazla izin kaldırır
     */
    public void removePermissionsFromRole(Long roleId, List<String> permissionKeys) {
        permissionKeys.forEach(permissionKey -> removePermissionFromRole(roleId, permissionKey));
    }
} 