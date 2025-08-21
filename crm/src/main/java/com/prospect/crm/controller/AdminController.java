package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.service.SystemLogService;
import com.prospect.crm.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final SystemLogService systemLogService;
    private final SubscriptionService subscriptionService;
    
    public AdminController(SystemLogService systemLogService, SubscriptionService subscriptionService) {
        this.systemLogService = systemLogService;
        this.subscriptionService = subscriptionService;
    }
    
    /**
     * Sistem durumu dashboard'u
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Log istatistikleri
            dashboard.put("totalLogs", systemLogService.getLogCount());
            dashboard.put("errorLogs", systemLogService.getLogCountByLevel("ERROR"));
            dashboard.put("warningLogs", systemLogService.getLogCountByLevel("WARN"));
            
            // Sistem bilgileri
            dashboard.put("systemInfo", getSystemInfo());
            
            return ResponseEntity.ok(ApiResponse.success(dashboard, "Dashboard data retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting dashboard data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get dashboard data", "ERR_1006", e.getMessage()));
        }
    }
    
    /**
     * Sistem bilgileri
     */
    @GetMapping("/system-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemInfoEndpoint() {
        try {
            Map<String, Object> systemInfo = getSystemInfo();
            
            return ResponseEntity.ok(ApiResponse.success(systemInfo, "System info retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting system info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get system info", "ERR_1006", e.getMessage()));
        }
    }
    
    /**
     * Log temizleme işlemi
     */
    @PostMapping("/cleanup-logs")
    public ResponseEntity<ApiResponse<String>> cleanupLogs() {
        try {
            systemLogService.cleanupOldLogsResponse();
            return ResponseEntity.ok(ApiResponse.success("Log cleanup completed", "Log cleanup completed successfully"));
            
        } catch (Exception e) {
            log.error("Error during log cleanup: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to cleanup logs", "ERR_1006", e.getMessage()));
        }
    }
    
    /**
     * Sistem performans metrikleri
     */
    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            
            // Memory kullanımı
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            metrics.put("memoryUsagePercent", (double) usedMemory / totalMemory * 100);
            metrics.put("memoryUsageMB", usedMemory / (1024 * 1024));
            metrics.put("totalMemoryMB", totalMemory / (1024 * 1024));
            
            // CPU kullanımı (basit hesaplama)
            metrics.put("availableProcessors", runtime.availableProcessors());
            
            return ResponseEntity.ok(ApiResponse.success(metrics, "Performance metrics retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting performance metrics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get performance metrics", "ERR_1006", e.getMessage()));
        }
    }
    
    /**
     * Abonelik yönetimi dashboard'u
     */
    @GetMapping("/subscription-dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionDashboard() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Grace period'da olan kullanıcılar
            List<UserSubsInfo> gracePeriodUsers = subscriptionService.getUsersInGracePeriod();
            dashboard.put("gracePeriodUsers", gracePeriodUsers.size());
            dashboard.put("gracePeriodUsersList", gracePeriodUsers);
            
            // Grace period'ı biten kullanıcılar
            List<UserSubsInfo> expiredGracePeriodUsers = subscriptionService.getUsersWithExpiredGracePeriod();
            dashboard.put("expiredGracePeriodUsers", expiredGracePeriodUsers.size());
            dashboard.put("expiredGracePeriodUsersList", expiredGracePeriodUsers);
            
            return ResponseEntity.ok(ApiResponse.success(dashboard, "Subscription dashboard retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting subscription dashboard: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get subscription dashboard", "ERR_1006", e.getMessage()));
        }
    }
    
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        info.put("totalMemory", runtime.totalMemory());
        info.put("freeMemory", runtime.freeMemory());
        info.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        info.put("maxMemory", runtime.maxMemory());
        info.put("availableProcessors", runtime.availableProcessors());
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("osName", System.getProperty("os.name"));
        
        return info;
    }
} 