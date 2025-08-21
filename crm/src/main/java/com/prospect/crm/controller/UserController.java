package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.dto.UserRequestDto;
import com.prospect.crm.mapper.UserMapper;
import com.prospect.crm.model.Users;
import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.UserService;
import com.prospect.crm.service.PermissionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;
    private final PermissionService permissionService;

    public UserController(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @GetMapping
    @HasPermission(PermissionConstants.USER_LIST)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserListDto>>> getAllUsers() {
        try {
            List<UserListDto> users = userService.getAll().stream().map(UserMapper::toUserList).collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<UserListDto>>builder()
                    .success(false)
                    .message("Failed to retrieve users: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{id}")
    @HasPermission(PermissionConstants.USER_READ)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserListDto>> getUserById(@PathVariable Long id) {
        try {
            UserListDto user = UserMapper.toUserList(userService.getById(id));
            return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
        } catch (Exception e) {
            log.error("Error retrieving user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<UserListDto>builder()
                    .success(false)
                    .message("Failed to retrieve user: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserListDto>> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            return userService.register(userRequestDto);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<UserListDto>builder()
                    .success(false)
                    .message("Failed to register user: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/{id}")
    @HasPermission(PermissionConstants.USER_UPDATE)
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserListDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        try {
            return userService.update(id, userRequestDto);
        } catch (Exception e) {
            log.error("Error updating user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<UserListDto>builder()
                    .success(false)
                    .message("Failed to update user: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{id}")
    @HasPermission(PermissionConstants.USER_DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            return userService.delete(id);
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete user: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcının izinlerini getirir
     */
    @GetMapping("/{id}/permissions")
    @HasPermission(PermissionConstants.USER_READ)
    public ResponseEntity<ApiResponse<Set<String>>> getUserPermissions(@PathVariable Long id) {
        try {
            Set<String> permissions = permissionService.getUserPermissions(id);
            
            return ResponseEntity.ok(ApiResponse.<Set<String>>builder()
                    .success(true)
                    .message("User permissions retrieved successfully")
                    .data(permissions)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving permissions for user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Set<String>>builder()
                    .success(false)
                    .message("Failed to retrieve user permissions: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcının belirli bir izne sahip olup olmadığını kontrol eder
     */
    @GetMapping("/{id}/permissions/check")
    @HasPermission(PermissionConstants.USER_READ)
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserPermission(
            @PathVariable Long id, 
            @RequestParam String permission) {
        try {
            boolean hasPermission = permissionService.hasPermission(id, permission);
            
            Map<String, Object> result = Map.of(
                "userId", id,
                "permission", permission,
                "hasPermission", hasPermission
            );
            
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Permission check completed")
                    .data(result)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error checking permission {} for user {}: {}", permission, id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Failed to check user permission: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcının durumunu değiştirir (aktif/pasif)
     */
    @PutMapping("/{id}/status")
    @HasPermission(PermissionConstants.USER_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changeUserStatus(
            @PathVariable Long id, 
            @RequestParam Boolean active) {
        try {
            // Bu fonksiyonalite UserService'e eklenebilir
            log.info("Changing status for user {} to {}", id, active);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("User status change - functionality to be implemented")
                    .data("Status change for user " + id + " to " + active)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error changing status for user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to change user status: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcıyı email ile arar
     */
    @GetMapping("/search/email")
    @HasPermission(PermissionConstants.USER_READ)
    public ResponseEntity<ApiResponse<UserListDto>> getUserByEmail(@RequestParam String email) {
        try {
            // Bu fonksiyonalite UserService'e eklenebilir
            log.info("Searching user by email: {}", email);
            
            return ResponseEntity.ok(ApiResponse.<UserListDto>builder()
                    .success(true)
                    .message("User search by email - functionality to be implemented")
                    .data(null)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error searching user by email {}: {}", email, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<UserListDto>builder()
                    .success(false)
                    .message("Failed to search user by email: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcıyı username ile arar
     */
    @GetMapping("/search/username")
    @HasPermission(PermissionConstants.USER_READ)
    public ResponseEntity<ApiResponse<UserListDto>> getUserByUsername(@RequestParam String username) {
        try {
            // Bu fonksiyonalite UserService'e eklenebilir
            log.info("Searching user by username: {}", username);
            
            return ResponseEntity.ok(ApiResponse.<UserListDto>builder()
                    .success(true)
                    .message("User search by username - functionality to be implemented")
                    .data(null)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error searching user by username {}: {}", username, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<UserListDto>builder()
                    .success(false)
                    .message("Failed to search user by username: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcının rolünü değiştirir
     */
    @PutMapping("/{id}/role")
    @HasPermission(PermissionConstants.USER_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> changeUserRole(
            @PathVariable Long id, 
            @RequestParam Long roleId) {
        try {
            // Bu fonksiyonalite UserService'e eklenebilir
            log.info("Changing role for user {} to role {}", id, roleId);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("User role change - functionality to be implemented")
                    .data("Role change for user " + id + " to role " + roleId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error changing role for user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to change user role: " + e.getMessage())
                    .build());
        }
    }

    /**
     * Kullanıcının şifresini sıfırlar
     */
    @PostMapping("/{id}/reset-password")
    @HasPermission(PermissionConstants.USER_UPDATE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetUserPassword(@PathVariable Long id) {
        try {
            // Bu fonksiyonalite UserService'e eklenebilir
            log.info("Resetting password for user {}", id);
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Password reset - functionality to be implemented")
                    .data("Password reset initiated for user " + id)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error resetting password for user {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to reset user password: " + e.getMessage())
                    .build());
        }
    }
}
