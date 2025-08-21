package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.Role;
import com.prospect.crm.model.RolePermission;
import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.RoleService;
import com.prospect.crm.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/v1/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    public RoleController(RoleService roleService, RolePermissionService rolePermissionService) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping
    @HasPermission(PermissionConstants.ROLE_LIST)
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAll();
            return ResponseEntity.ok(ApiResponse.success(roles, "Roles retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving roles: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<Role>>builder()
                    .success(false)
                    .message("Failed to retrieve roles: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{id}")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        try {
            Role role = roleService.getById(id);
            return ResponseEntity.ok(ApiResponse.success(role, "Role retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Role>builder()
                    .success(false)
                    .message("Failed to retrieve role: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping
    @HasPermission(PermissionConstants.ROLE_CREATE)
    public ResponseEntity<ApiResponse<Role>> createRole(@Valid @RequestBody Role role) {
        try {
            return roleService.create(role);
        } catch (Exception e) {
            log.error("Error creating role: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Role>builder()
                    .success(false)
                    .message("Failed to create role: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{id}")
    @HasPermission(PermissionConstants.ROLE_UPDATE)
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
        try {
            return roleService.update(id, role);
        } catch (Exception e) {
            log.error("Error updating role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Role>builder()
                    .success(false)
                    .message("Failed to update role: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    @HasPermission(PermissionConstants.ROLE_DELETE)
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
        try {
            roleService.delete(id);
            return ResponseEntity.ok().body(ApiResponse.success("Role deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to delete role: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'a atanmış izinleri getirir
     */
    @GetMapping("/{id}/permissions")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<List<RolePermission>>> getRolePermissions(@PathVariable Long id) {
        try {
            List<RolePermission> permissions = rolePermissionService.getRolePermissions(id);
            
            return ResponseEntity.ok(ApiResponse.<List<RolePermission>>builder()
                    .success(true)
                    .message("Role permissions retrieved successfully")
                    .data(permissions)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving permissions for role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<RolePermission>>builder()
                    .success(false)
                    .message("Failed to retrieve role permissions: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'a atanmış izin key'lerini getirir
     */
    @GetMapping("/{id}/permission-keys")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<Set<String>>> getRolePermissionKeys(@PathVariable Long id) {
        try {
            Set<String> permissionKeys = rolePermissionService.getRolePermissionKeys(id);
            
            return ResponseEntity.ok(ApiResponse.<Set<String>>builder()
                    .success(true)
                    .message("Role permission keys retrieved successfully")
                    .data(permissionKeys)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving permission keys for role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Set<String>>builder()
                    .success(false)
                    .message("Failed to retrieve role permission keys: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'a tek bir izin atar
     */
    @PostMapping("/{id}/permissions/{permissionKey}")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<RolePermission>> assignPermissionToRole(
            @PathVariable Long id, 
            @PathVariable String permissionKey) {
        try {
            RolePermission rolePermission = rolePermissionService.addPermissionToRole(id, permissionKey);
            
            if (rolePermission == null) {
                return ResponseEntity.badRequest().body(ApiResponse.<RolePermission>builder()
                        .success(false)
                        .message("Permission already exists for this role")
                        .build());
            }
            
            return ResponseEntity.ok(ApiResponse.<RolePermission>builder()
                    .success(true)
                    .message("Permission assigned to role successfully")
                    .data(rolePermission)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error assigning permission {} to role {}: {}", permissionKey, id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<RolePermission>builder()
                    .success(false)
                    .message("Failed to assign permission to role: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'dan tek bir izin kaldırır
     */
    @DeleteMapping("/{id}/permissions/{permissionKey}")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_REVOKE)
    public ResponseEntity<ApiResponse<String>> revokePermissionFromRole(
            @PathVariable Long id, 
            @PathVariable String permissionKey) {
        try {
            rolePermissionService.removePermissionFromRole(id, permissionKey);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Permission revoked from role successfully")
                    .data("Permission " + permissionKey + " revoked from role " + id)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error revoking permission {} from role {}: {}", permissionKey, id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to revoke permission from role: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'a birden fazla izin atar
     */
    @PostMapping("/{id}/permissions/batch")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<String>> assignPermissionsToRole(
            @PathVariable Long id, 
            @RequestBody List<String> permissionKeys) {
        try {
            rolePermissionService.addPermissionsToRole(id, permissionKeys);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Permissions assigned to role successfully")
                    .data("Assigned " + permissionKeys.size() + " permissions to role " + id)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error assigning permissions to role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to assign permissions to role: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'dan birden fazla izin kaldırır
     */
    @DeleteMapping("/{id}/permissions/batch")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_REVOKE)
    public ResponseEntity<ApiResponse<String>> revokePermissionsFromRole(
            @PathVariable Long id, 
            @RequestBody List<String> permissionKeys) {
        try {
            rolePermissionService.removePermissionsFromRole(id, permissionKeys);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Permissions revoked from role successfully")
                    .data("Revoked " + permissionKeys.size() + " permissions from role " + id)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error revoking permissions from role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to revoke permissions from role: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'ın tüm izinlerini temizler
     */
    @DeleteMapping("/{id}/permissions/all")
    @HasPermission(PermissionConstants.ROLE_PERMISSION_REVOKE)
    public ResponseEntity<ApiResponse<String>> clearRolePermissions(@PathVariable Long id) {
        try {
            rolePermissionService.clearRolePermissions(id);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("All permissions cleared for role successfully")
                    .data("All permissions cleared for role " + id)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error clearing permissions for role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to clear role permissions: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'ın belirli bir izne sahip olup olmadığını kontrol eder
     */
    @GetMapping("/{id}/permissions/check")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkRolePermission(
            @PathVariable Long id, 
            @RequestParam String permission) {
        try {
            Set<String> rolePermissions = rolePermissionService.getRolePermissionKeys(id);
            boolean hasPermission = rolePermissions.contains(permission);
            
            Map<String, Object> result = Map.of(
                "roleId", id,
                "permission", permission,
                "hasPermission", hasPermission
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Role permission check completed")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error checking permission {} for role {}: {}", permission, id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Failed to check role permission: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Role'a atanmış kullanıcı sayısını getirir
     */
    @GetMapping("/{id}/user-count")
    @HasPermission(PermissionConstants.ROLE_READ)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRoleUserCount(@PathVariable Long id) {
        try {
            // Bu fonksiyonalite RoleService'e eklenebilir
            log.info("Getting user count for role: {}", id);
            
            Map<String, Object> result = Map.of(
                "roleId", id,
                "userCount", 0,
                "note", "Functionality to be implemented"
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Role user count - functionality to be implemented")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error getting user count for role {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Failed to get role user count: " + e.getMessage())
                    .build());
        }
    }
}
