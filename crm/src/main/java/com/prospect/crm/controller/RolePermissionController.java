package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.RolePermission;
import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/v1/role-permissions")
@PreAuthorize("hasRole('ADMIN')")
public class RolePermissionController {
    
    private final RolePermissionService rolePermissionService;
    
    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }
    
    /**
     * Role'ın tüm izinlerini getirir
     */
    @GetMapping("/role/{roleId}")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<List<RolePermission>>> getRolePermissions(@PathVariable Long roleId) {
        try {
            List<RolePermission> permissions = rolePermissionService.getRolePermissions(roleId);
            
            return ResponseEntity.ok(ApiResponse.<List<RolePermission>>builder()
                    .success(true)
                    .message("Role permissions retrieved successfully")
                    .data(permissions)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving permissions for role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<RolePermission>>builder()
                    .success(false)
                    .message("Failed to retrieve role permissions")
                    .build());
        }
    }
    
    /**
     * Role'ın permission key'lerini getirir
     */
    @GetMapping("/role/{roleId}/keys")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<Set<String>>> getRolePermissionKeys(@PathVariable Long roleId) {
        try {
            Set<String> permissionKeys = rolePermissionService.getRolePermissionKeys(roleId);
            
            return ResponseEntity.ok(ApiResponse.<Set<String>>builder()
                    .success(true)
                    .message("Role permission keys retrieved successfully")
                    .data(permissionKeys)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving permission keys for role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Set<String>>builder()
                    .success(false)
                    .message("Failed to retrieve role permission keys")
                    .build());
        }
    }
    
    /**
     * Role'a izin ekler
     */
    @PostMapping("/role/{roleId}/permission/{permissionKey}")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<RolePermission>> addPermissionToRole(
            @PathVariable Long roleId, 
            @PathVariable String permissionKey) {
        try {
            RolePermission rolePermission = rolePermissionService.addPermissionToRole(roleId, permissionKey);
            
            if (rolePermission == null) {
                return ResponseEntity.badRequest().body(ApiResponse.<RolePermission>builder()
                        .success(false)
                        .message("Permission already exists for this role")
                        .build());
            }
            
            return ResponseEntity.ok(ApiResponse.<RolePermission>builder()
                    .success(true)
                    .message("Permission added to role successfully")
                    .data(rolePermission)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error adding permission {} to role {}: {}", permissionKey, roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<RolePermission>builder()
                    .success(false)
                    .message("Failed to add permission to role")
                    .build());
        }
    }
    
    /**
     * Role'dan izin kaldırır
     */
    @DeleteMapping("/role/{roleId}/permission/{permissionKey}")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_REVOKE)
    public ResponseEntity<ApiResponse<String>> removePermissionFromRole(
            @PathVariable Long roleId, 
            @PathVariable String permissionKey) {
        try {
            rolePermissionService.removePermissionFromRole(roleId, permissionKey);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Permission removed from role successfully")
                    .data("Permission " + permissionKey + " removed from role " + roleId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error removing permission {} from role {}: {}", permissionKey, roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to remove permission from role")
                    .build());
        }
    }
    
    /**
     * Role'ın tüm izinlerini siler
     */
    @DeleteMapping("/role/{roleId}/permissions")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_REVOKE)
    public ResponseEntity<ApiResponse<String>> clearRolePermissions(@PathVariable Long roleId) {
        try {
            rolePermissionService.clearRolePermissions(roleId);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("All permissions cleared for role successfully")
                    .data("All permissions cleared for role " + roleId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error clearing permissions for role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to clear role permissions")
                    .build());
        }
    }
    
    /**
     * Role'a birden fazla izin ekler
     */
    @PostMapping("/role/{roleId}/permissions")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<String>> addPermissionsToRole(
            @PathVariable Long roleId, 
            @RequestBody List<String> permissionKeys) {
        try {
            rolePermissionService.addPermissionsToRole(roleId, permissionKeys);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Permissions added to role successfully")
                    .data("Added " + permissionKeys.size() + " permissions to role " + roleId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error adding permissions to role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to add permissions to role")
                    .build());
        }
    }
    
    /**
     * Role'dan birden fazla izin kaldırır
     */
    @DeleteMapping("/role/{roleId}/permissions/batch")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_REVOKE)
    public ResponseEntity<ApiResponse<String>> removePermissionsFromRole(
            @PathVariable Long roleId, 
            @RequestBody List<String> permissionKeys) {
        try {
            rolePermissionService.removePermissionsFromRole(roleId, permissionKeys);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Permissions removed from role successfully")
                    .data("Removed " + permissionKeys.size() + " permissions from role " + roleId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error removing permissions from role {}: {}", roleId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to remove permissions from role")
                    .build());
        }
    }
} 