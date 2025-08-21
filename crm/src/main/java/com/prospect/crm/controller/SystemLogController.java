package com.prospect.crm.controller;

import com.prospect.crm.constant.LogLevel;
import com.prospect.crm.constant.LogType;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.PaginationInfo;
import com.prospect.crm.model.SystemLog;
import com.prospect.crm.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/v1/logs")
@PreAuthorize("hasRole('ADMIN')")
public class SystemLogController {
    private final SystemLogService systemLogService;

    public SystemLogController(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemLog>>> getAllLogs() {
        return systemLogService.getAllLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SystemLog>> getLogById(@PathVariable Long id) {
        return systemLogService.getLogById(id);
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByLevel(@PathVariable LogLevel level) {
        return systemLogService.getLogsByLevel(level);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByType(@PathVariable LogType type) {
        return systemLogService.getLogsByType(type);
    }

    @GetMapping("/errors")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getErrorLogs() {
        return systemLogService.getErrorLogs();
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SystemLog>>> searchLogs(@RequestParam String keyword) {
        return systemLogService.searchLogs(keyword);
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return systemLogService.getLogsByDateRange(startDate, endDate);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByUserId(@PathVariable String userId) {
        List<SystemLog> logs = systemLogService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(logs, "User logs retrieved successfully"));
    }

    @GetMapping("/slow-queries")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getSlowQueries(@RequestParam(defaultValue = "1000") Long threshold) {
        List<SystemLog> logs = systemLogService.findSlowQueries(threshold);
        return ResponseEntity.ok(ApiResponse.success(logs, "Slow queries retrieved successfully"));
    }

    // Sayfalama ile sorgular
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemLog> logPage = systemLogService.findByLevel(LogLevel.INFO, pageable);
        
        PaginationInfo pagination = PaginationInfo.of(page, size, logPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.successWithPagination(logPage.getContent(), pagination));
    }

    @GetMapping("/page/level/{level}")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByLevelWithPagination(
            @PathVariable LogLevel level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemLog> logPage = systemLogService.findByLevel(level, pageable);
        
        PaginationInfo pagination = PaginationInfo.of(page, size, logPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.successWithPagination(logPage.getContent(), pagination));
    }

    @GetMapping("/page/type/{type}")
    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByTypeWithPagination(
            @PathVariable LogType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SystemLog> logPage = systemLogService.findByType(type, pageable);
        
        PaginationInfo pagination = PaginationInfo.of(page, size, logPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.successWithPagination(logPage.getContent(), pagination));
    }

    // İstatistik endpoint'leri
    @GetMapping("/stats/level-count")
    public ResponseEntity<ApiResponse<List<Object[]>>> getLogCountByLevel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> stats = systemLogService.getLogCountByLevel(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats, "Log count by level retrieved successfully"));
    }

    @GetMapping("/stats/type-count")
    public ResponseEntity<ApiResponse<List<Object[]>>> getLogCountByType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> stats = systemLogService.getLogCountByType(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(stats, "Log count by type retrieved successfully"));
    }

    @GetMapping("/stats/error-count")
    public ResponseEntity<ApiResponse<Long>> getErrorCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate) {
        Long count = systemLogService.countByLevelAndDateAfter(LogLevel.ERROR, startDate);
        return ResponseEntity.ok(ApiResponse.success(count, "Error count retrieved successfully"));
    }

    // Manuel log kaydetme endpoint'leri (sadece test amaçlı)
    @PostMapping("/test/info")
    public ResponseEntity<ApiResponse<String>> logTestInfo(@RequestParam String message) {
        systemLogService.logInfo(message, "Test log", "SystemLogController", "logTestInfo");
        return ResponseEntity.ok(ApiResponse.success("Info log created successfully"));
    }

    @PostMapping("/test/warn")
    public ResponseEntity<ApiResponse<String>> logTestWarn(@RequestParam String message) {
        systemLogService.logWarn(message, "Test log", "SystemLogController", "logTestWarn");
        return ResponseEntity.ok(ApiResponse.success("Warning log created successfully"));
    }

    @PostMapping("/test/error")
    public ResponseEntity<ApiResponse<String>> logTestError(@RequestParam String message) {
        systemLogService.logError(message, "Test log", "Test stack trace", "SystemLogController", "logTestError");
        return ResponseEntity.ok(ApiResponse.success("Error log created successfully"));
    }
    
    // Log temizleme endpoint'leri
    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<String>> cleanupOldLogs() {
        return systemLogService.cleanupOldLogsResponse();
    }
    
    @GetMapping("/cleanup/count")
    public ResponseEntity<ApiResponse<Long>> getOldLogsCount() {
        Long count = systemLogService.getOldLogsCount();
        return ResponseEntity.ok(ApiResponse.success(count, "Old logs count retrieved successfully"));
    }
    
    @PostMapping("/cleanup/manual")
    public ResponseEntity<ApiResponse<String>> manualCleanup(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String type) {
        
        try {
            int deletedCount = 0;
            LocalDateTime cutoffDate;
            
            if (days != null) {
                cutoffDate = LocalDateTime.now().minusDays(days);
            } else {
                cutoffDate = LocalDateTime.now().minusMonths(1); // Default 1 ay
            }
            
            if (level != null) {
                LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
                deletedCount = systemLogService.deleteOldLogsByLevel(cutoffDate, logLevel);
            } else if (type != null) {
                LogType logType = LogType.valueOf(type.toUpperCase());
                deletedCount = systemLogService.deleteOldLogsByType(cutoffDate, logType);
            } else {
                deletedCount = systemLogService.deleteOldNonErrorLogs(cutoffDate);
            }
            
            String message = String.format("Manual cleanup completed. Deleted %d logs older than %s", 
                deletedCount, cutoffDate.toString());
            
            return ResponseEntity.ok(ApiResponse.success(message));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Manual cleanup failed", "ERR_12007", e.getMessage()));
        }
    }
    
    @PostMapping("/cleanup/emergency")
    public ResponseEntity<ApiResponse<String>> emergencyCleanup(
            @RequestParam Integer days) {
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            int deletedCount = systemLogService.deleteAllOldLogs(cutoffDate);
            
            String message = String.format("Emergency cleanup completed. Deleted %d logs older than %d days", 
                deletedCount, days);
            
            // Acil durum temizliği logu
            systemLogService.logWarn(
                "Emergency log cleanup performed",
                message,
                "SystemLogController",
                "emergencyCleanup"
            );
            
            return ResponseEntity.ok(ApiResponse.success(message));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Emergency cleanup failed", "ERR_12008", e.getMessage()));
        }
    }
} 