package com.prospect.crm.security;

import com.prospect.crm.exception.AccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PermissionAspect {
    
    private final PermissionEvaluator permissionEvaluator;
    
    public PermissionAspect(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }
    
    /**
     * @HasPermission annotation'ını işler
     */
    @Before("@annotation(hasPermission)")
    public void checkPermission(JoinPoint joinPoint, HasPermission hasPermission) {
        try {
            boolean hasAccess = false;
            
            // Tekil izin kontrolü
            if (!hasPermission.value().isEmpty()) {
                hasAccess = permissionEvaluator.hasPermission(hasPermission.value());
            }
            
            // Birden fazla izin kontrolü (herhangi biri)
            if (!hasAccess && hasPermission.any().length > 0) {
                hasAccess = permissionEvaluator.hasAnyPermission(hasPermission.any());
            }
            
            // Birden fazla izin kontrolü (hepsi)
            if (!hasAccess && hasPermission.all().length > 0) {
                hasAccess = permissionEvaluator.hasAllPermissions(hasPermission.all());
            }
            
            if (!hasAccess) {
                log.warn("Access denied for method {} - required permission: {}", 
                    joinPoint.getSignature().getName(), hasPermission.value());
                throw new AccessDeniedException("Access denied - insufficient permissions");
            }
            
            log.debug("Permission check passed for method {} - permission: {}", 
                joinPoint.getSignature().getName(), hasPermission.value());
                
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error checking permission for method {}: {}", 
                joinPoint.getSignature().getName(), e.getMessage());
            throw new AccessDeniedException("Error checking permissions");
        }
    }
} 