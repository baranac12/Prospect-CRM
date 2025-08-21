# Role-Based Permission System

## Overview

The role-based permission system provides fine-grained access control for the CRM application. It allows administrators to assign specific permissions to roles, and users inherit these permissions based on their assigned role.

## Architecture

### Core Components

1. **RolePermission Entity** - Database model for storing role-permission relationships
2. **PermissionService** - Service for checking user permissions
3. **PermissionEvaluator** - Component for evaluating permissions in security context
4. **HasPermission Annotation** - Custom annotation for method-level permission control
5. **PermissionAspect** - AOP aspect for handling permission checks
6. **RolePermissionService** - Service for managing role permissions
7. **RolePermissionController** - REST API for managing role permissions

### Database Structure

```sql
-- Role permissions table
CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT REFERENCES roles(id),
    permission_key VARCHAR(255) NOT NULL,
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Permission Constants

All permission keys are defined in `PermissionConstants.java`:

### User Management
- `user:create` - Create users
- `user:read` - Read user information
- `user:update` - Update user information
- `user:delete` - Delete users
- `user:list` - List all users

### Lead Management
- `lead:create` - Create leads
- `lead:read` - Read lead information
- `lead:update` - Update lead information
- `lead:delete` - Delete leads
- `lead:list` - List all leads
- `lead:export` - Export leads
- `lead:import` - Import leads

### Email Management
- `email:send` - Send emails
- `email:read` - Read emails
- `email:delete` - Delete emails
- `email:list` - List emails
- `email:draft:create` - Create email drafts
- `email:draft:read` - Read email drafts
- `email:draft:update` - Update email drafts
- `email:draft:delete` - Delete email drafts
- `email:draft:list` - List email drafts

### Subscription Management
- `subscription:create` - Create subscriptions
- `subscription:read` - Read subscription information
- `subscription:update` - Update subscriptions
- `subscription:delete` - Delete subscriptions
- `subscription:list` - List subscriptions
- `subscription:cancel` - Cancel subscriptions
- `subscription:renew` - Renew subscriptions

### Payment Management
- `payment:create` - Create payments
- `payment:read` - Read payment information
- `payment:update` - Update payments
- `payment:delete` - Delete payments
- `payment:list` - List payments
- `payment:refund` - Process refunds

### Robot Management
- `robot:create` - Create robots
- `robot:read` - Read robot information
- `robot:update` - Update robots
- `robot:delete` - Delete robots
- `robot:list` - List robots
- `robot:start` - Start robots
- `robot:stop` - Stop robots

### System Management (Admin Only)
- `system:log:read` - Read system logs
- `system:log:delete` - Delete system logs
- `system:config:read` - Read system configuration
- `system:config:update` - Update system configuration
- `system:backup` - Create system backups
- `system:restore` - Restore system backups

### Role Management (Admin Only)
- `role:create` - Create roles
- `role:read` - Read role information
- `role:update` - Update roles
- `role:delete` - Delete roles
- `role:list` - List roles
- `role:permission:assign` - Assign permissions to roles
- `role:permission:revoke` - Revoke permissions from roles

## Usage

### Method-Level Permission Control

Use the `@HasPermission` annotation to control access to methods:

```java
@RestController
@RequestMapping("/v1/leads")
public class LeadController {
    
    @GetMapping
    @HasPermission(PermissionConstants.LEAD_LIST)
    public ResponseEntity<ApiResponse<List<Lead>>> getLeads() {
        // Method implementation
    }
    
    @PostMapping
    @HasPermission(PermissionConstants.LEAD_CREATE)
    public ResponseEntity<ApiResponse<Lead>> createLead(@RequestBody Lead lead) {
        // Method implementation
    }
    
    @PutMapping("/{id}")
    @HasPermission(PermissionConstants.LEAD_UPDATE)
    public ResponseEntity<ApiResponse<Lead>> updateLead(@PathVariable Long id, @RequestBody Lead lead) {
        // Method implementation
    }
    
    @DeleteMapping("/{id}")
    @HasPermission(PermissionConstants.LEAD_DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable Long id) {
        // Method implementation
    }
}
```

### Multiple Permission Requirements

You can require multiple permissions using the `any` or `all` attributes:

```java
// Require any of the specified permissions
@HasPermission(value = "", any = {PermissionConstants.LEAD_READ, PermissionConstants.LEAD_UPDATE})
public ResponseEntity<ApiResponse<Lead>> getOrUpdateLead() {
    // Method implementation
}

// Require all of the specified permissions
@HasPermission(value = "", all = {PermissionConstants.LEAD_READ, PermissionConstants.LEAD_EXPORT})
public ResponseEntity<ApiResponse<Lead>> exportLead() {
    // Method implementation
}
```

### Programmatic Permission Checking

You can also check permissions programmatically:

```java
@Service
public class LeadService {
    
    private final PermissionService permissionService;
    
    public LeadService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
    
    public void processLead(Long userId, Long leadId) {
        // Check if user has permission to read the lead
        if (!permissionService.hasPermission(userId, PermissionConstants.LEAD_READ)) {
            throw new AccessDeniedException("Insufficient permissions to read lead");
        }
        
        // Process the lead
        // ...
    }
}
```

## API Endpoints

### Role Permission Management

All role permission management endpoints require admin role and appropriate permissions:

#### Get Role Permissions
```
GET /v1/role-permissions/role/{roleId}
```
Returns all permissions assigned to a specific role.

#### Get Role Permission Keys
```
GET /v1/role-permissions/role/{roleId}/keys
```
Returns permission keys (strings) assigned to a specific role.

#### Add Permission to Role
```
POST /v1/role-permissions/role/{roleId}/permission/{permissionKey}
```
Adds a specific permission to a role.

#### Remove Permission from Role
```
DELETE /v1/role-permissions/role/{roleId}/permission/{permissionKey}
```
Removes a specific permission from a role.

#### Clear All Role Permissions
```
DELETE /v1/role-permissions/role/{roleId}/permissions
```
Removes all permissions from a role.

#### Add Multiple Permissions to Role
```
POST /v1/role-permissions/role/{roleId}/permissions
Content-Type: application/json

["lead:read", "lead:create", "lead:update"]
```
Adds multiple permissions to a role.

#### Remove Multiple Permissions from Role
```
DELETE /v1/role-permissions/role/{roleId}/permissions/batch
Content-Type: application/json

["lead:delete", "lead:export"]
```
Removes multiple permissions from a role.

## Security Configuration

The system uses a hybrid approach:

1. **Role-based access control** at the HTTP level (Spring Security)
2. **Permission-based access control** at the method level (custom annotations)

### HTTP Level Security

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/v1/auth/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/v1/logs/**").hasRole("ADMIN")
                .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                
                // User endpoints (basic role check)
                .requestMatchers("/v1/users/**").hasRole("USER")
                .requestMatchers("/v1/leads/**").hasRole("USER")
                
                // Default - authenticated users
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

### Method Level Security

The `@HasPermission` annotation provides fine-grained control:

```java
@HasPermission(PermissionConstants.LEAD_CREATE)
public ResponseEntity<ApiResponse<Lead>> createLead(@RequestBody Lead lead) {
    // Only users with 'lead:create' permission can access this method
}
```

## Error Handling

### Access Denied Exception

When a user lacks required permissions, an `AccessDeniedException` is thrown:

```java
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

The `GlobalExceptionHandler` catches `AccessDeniedException` and returns appropriate HTTP responses:

```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    ErrorResponse errorResponse = new ErrorResponse(
        ErrorCode.ACCESS_DENIED,
        "Access denied",
        e.getMessage()
    );
    
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
}
```

## Best Practices

### 1. Permission Naming Convention

Use a consistent naming convention for permissions:
- Format: `resource:action`
- Examples: `lead:create`, `user:read`, `email:send`

### 2. Granular Permissions

Create specific permissions rather than broad ones:
- ✅ Good: `lead:read`, `lead:create`, `lead:update`, `lead:delete`
- ❌ Bad: `lead:manage` (too broad)

### 3. Default Permissions

Assign default permissions to roles during system initialization:

```java
@Service
public class SystemInitializationService {
    
    public void initializeDefaultPermissions() {
        // Admin role gets all permissions
        List<String> adminPermissions = Arrays.asList(
            PermissionConstants.USER_CREATE,
            PermissionConstants.USER_READ,
            // ... all permissions
        );
        
        // User role gets basic permissions
        List<String> userPermissions = Arrays.asList(
            PermissionConstants.LEAD_READ,
            PermissionConstants.LEAD_CREATE,
            PermissionConstants.EMAIL_SEND
        );
        
        rolePermissionService.addPermissionsToRole(ADMIN_ROLE_ID, adminPermissions);
        rolePermissionService.addPermissionsToRole(USER_ROLE_ID, userPermissions);
    }
}
```

### 4. Permission Caching

Consider implementing permission caching for better performance:

```java
@Service
public class CachedPermissionService {
    
    private final Cache<Long, Set<String>> userPermissionCache;
    
    public boolean hasPermission(Long userId, String permissionKey) {
        Set<String> permissions = userPermissionCache.get(userId, () -> 
            permissionService.getUserPermissions(userId));
        
        return permissions.contains(permissionKey);
    }
}
```

### 5. Audit Logging

Log permission checks for security auditing:

```java
@Aspect
@Component
public class PermissionAuditAspect {
    
    @AfterThrowing("@annotation(hasPermission)")
    public void logPermissionDenied(JoinPoint joinPoint, HasPermission hasPermission) {
        // Log failed permission attempts
        systemLogService.logSecurity(
            "Permission denied",
            "Method: " + joinPoint.getSignature().getName() + 
            ", Required permission: " + hasPermission.value(),
            getCurrentUserId(),
            getCurrentUserIp(),
            getCurrentUserAgent()
        );
    }
}
```

## Testing

### Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {
    
    @Mock
    private RolePermissionRepository rolePermissionRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private PermissionService permissionService;
    
    @Test
    void hasPermission_WhenUserHasPermission_ReturnsTrue() {
        // Given
        Long userId = 1L;
        String permissionKey = "lead:read";
        Users user = createUserWithRole(1L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rolePermissionRepository.existsByRoleIdIdAndPermissionKey(1L, permissionKey))
            .thenReturn(true);
        
        // When
        boolean result = permissionService.hasPermission(userId, permissionKey);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    void hasPermission_WhenUserDoesNotHavePermission_ReturnsFalse() {
        // Given
        Long userId = 1L;
        String permissionKey = "lead:read";
        Users user = createUserWithRole(1L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(rolePermissionRepository.existsByRoleIdIdAndPermissionKey(1L, permissionKey))
            .thenReturn(false);
        
        // When
        boolean result = permissionService.hasPermission(userId, permissionKey);
        
        // Then
        assertFalse(result);
    }
}
```

### Integration Tests

```java
@SpringBootTest
@AutoConfigureTestDatabase
class RolePermissionIntegrationTest {
    
    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Test
    void testPermissionFlow() {
        // Given
        Long roleId = 1L;
        String permissionKey = "lead:read";
        
        // When
        rolePermissionService.addPermissionToRole(roleId, permissionKey);
        
        // Then
        Set<String> permissions = rolePermissionService.getRolePermissionKeys(roleId);
        assertTrue(permissions.contains(permissionKey));
    }
}
```

## Performance Considerations

### Database Indexing

Add indexes to the `role_permissions` table for better performance:

```sql
-- Index for permission lookups
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);

-- Index for permission key lookups
CREATE INDEX idx_role_permissions_permission_key ON role_permissions(permission_key);

-- Composite index for role-permission lookups
CREATE INDEX idx_role_permissions_role_permission ON role_permissions(role_id, permission_key);
```

### Caching Strategy

Implement a multi-level caching strategy:

1. **User Permission Cache** - Cache user permissions in memory
2. **Role Permission Cache** - Cache role permissions in Redis
3. **Permission Key Cache** - Cache frequently used permission keys

### Database Optimization

- Use batch operations for bulk permission assignments
- Implement soft deletes for permission history
- Use database views for complex permission queries

## Monitoring and Metrics

### Permission Usage Metrics

Track permission usage for optimization:

```java
@Component
public class PermissionMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordPermissionCheck(String permissionKey, boolean granted) {
        meterRegistry.counter("permission.checks", 
            "permission", permissionKey, 
            "result", granted ? "granted" : "denied")
            .increment();
    }
}
```

### Performance Monitoring

Monitor permission check performance:

```java
@Aspect
@Component
public class PermissionPerformanceAspect {
    
    @Around("@annotation(hasPermission)")
    public Object measurePermissionCheck(ProceedingJoinPoint joinPoint, HasPermission hasPermission) {
        long startTime = System.currentTimeMillis();
        
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            // Log or record performance metrics
        }
    }
}
```

## Future Enhancements

### 1. Dynamic Permissions

Support for dynamic permission creation and management through the API.

### 2. Permission Inheritance

Support for permission inheritance between roles.

### 3. Time-based Permissions

Support for temporary permissions with expiration dates.

### 4. Resource-level Permissions

Support for permissions on specific resources (e.g., user can only read their own leads).

### 5. Permission Groups

Support for grouping related permissions for easier management.

### 6. Advanced Permission Expressions

Support for complex permission expressions using logical operators.

## Conclusion

The role-based permission system provides a robust, scalable, and secure way to manage access control in the CRM application. It combines the simplicity of role-based access control with the flexibility of permission-based access control, allowing for fine-grained security management while maintaining good performance and usability. 