package com.prospect.crm.service;

import com.prospect.crm.constant.LogLevel;
import com.prospect.crm.constant.LogType;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.SystemLog;
import com.prospect.crm.repository.SystemLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SystemLogService {
    private final SystemLogRepository systemLogRepository;

    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }


    public void logInfo(String message, String details, String className, String methodName) {
        saveLog(LogLevel.INFO, LogType.SYSTEM, message, details, null, className, methodName, null, null, null, null, null, null, null, null, null, null);
    }

    public void logWarn(String message, String details, String className, String methodName) {
        saveLog(LogLevel.WARN, LogType.SYSTEM, message, details, null, className, methodName, null, null, null, null, null, null, null, null, null, null);
    }

    public void logError(String message, String details, String stackTrace, String className, String methodName) {
        saveLog(LogLevel.ERROR, LogType.ERROR, message, details, stackTrace, className, methodName, null, null, null, null, null, null, null, null, null, null);
    }

    public void logSecurity(String message, String details, String userId, String ipAddress, String userAgent) {
        saveLog(LogLevel.INFO, LogType.SECURITY, message, details, null, null, null, userId, ipAddress, userAgent, null, null, null, null, null, null, null);
    }

    public void logApi(String message, String endpoint, String httpMethod, Integer httpStatus, String requestBody, String responseBody, Long executionTime, String userId, String ipAddress) {
        saveLog(LogLevel.INFO, LogType.API, message, null, null, null, null, userId, ipAddress, null, executionTime, UUID.randomUUID().toString(), endpoint, httpMethod, httpStatus, requestBody, responseBody);
    }

    public void logPerformance(String message, Long executionTime, String className, String methodName) {
        saveLog(LogLevel.INFO, LogType.PERFORMANCE, message, null, null, className, methodName, null, null, null, executionTime, null, null, null, null, null, null);
    }

    public void logBusiness(String message, String details, String userId) {
        saveLog(LogLevel.INFO, LogType.BUSINESS, message, details, null, null, null, userId, null, null, null, null, null, null, null, null, null);
    }

    public void logAudit(String message, String details, String userId, String ipAddress) {
        saveLog(LogLevel.INFO, LogType.AUDIT, message, details, null, null, null, userId, ipAddress, null, null, null, null, null, null, null, null);
    }

    public void logDatabase(String message, String details, String className, String methodName) {
        saveLog(LogLevel.INFO, LogType.DATABASE, message, details, null, className, methodName, null, null, null, null, null, null, null, null, null, null);
    }

    public void logExternalService(String message, String details, String className, String methodName) {
        saveLog(LogLevel.INFO, LogType.EXTERNAL_SERVICE, message, details, null, className, methodName, null, null, null, null, null, null, null, null, null, null);
    }


    private void saveLog(LogLevel level, LogType type, String message, String details, String stackTrace,
                        String className, String methodName, String userId, String ipAddress, String userAgent,
                        Long executionTime, String requestId, String endpoint, String httpMethod, Integer httpStatus,
                        String requestBody, String responseBody) {
        try {
            SystemLog systemLog = new SystemLog();
            systemLog.setLevel(level);
            systemLog.setType(type);
            systemLog.setMessage(truncateString(message, 1000));
            systemLog.setDetails(truncateString(details, 4000));
            systemLog.setStackTrace(stackTrace);
            systemLog.setClassName(truncateString(className, 255));
            systemLog.setMethodName(truncateString(methodName, 255));
            systemLog.setUserId(truncateString(userId, 50));
            systemLog.setIpAddress(truncateString(ipAddress, 45));
            systemLog.setUserAgent(truncateString(userAgent, 500));
            systemLog.setTimestamp(LocalDateTime.now());
            systemLog.setExecutionTime(executionTime);
            systemLog.setRequestId(truncateString(requestId, 100));
            systemLog.setEndpoint(truncateString(endpoint, 500));
            systemLog.setHttpMethod(truncateString(httpMethod, 10));
            systemLog.setHttpStatus(httpStatus);
            systemLog.setRequestBody(requestBody);
            systemLog.setResponseBody(responseBody);

            systemLogRepository.save(systemLog);
        } catch (Exception e) {
            log.error("Failed to save system log: {}", e.getMessage(), e);
        }
    }

    private String truncateString(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }


    public List<SystemLog> findAll() {
        return systemLogRepository.findAll();
    }

    public SystemLog findById(Long id) {
        return systemLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Log not found with id: " + id));
    }

    public List<SystemLog> findByLevel(LogLevel level) {
        return systemLogRepository.findByLevel(level);
    }

    public List<SystemLog> findByType(LogType type) {
        return systemLogRepository.findByType(type);
    }

    public List<SystemLog> findByLevelAndType(LogLevel level, LogType type) {
        return systemLogRepository.findByLevelAndType(level, type);
    }

    public List<SystemLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return systemLogRepository.findByTimestampBetween(startDate, endDate);
    }

    public List<SystemLog> findByUserId(String userId) {
        return systemLogRepository.findByUserId(userId);
    }

    public List<SystemLog> findByKeyword(String keyword) {
        return systemLogRepository.findByKeyword(keyword);
    }

    public List<SystemLog> findErrors() {
        return systemLogRepository.findByLevelIn(List.of(LogLevel.ERROR, LogLevel.FATAL));
    }

    public List<SystemLog> findSlowQueries(Long threshold) {
        return systemLogRepository.findByExecutionTimeGreaterThan(threshold);
    }

    // Sayfalama ile sorgular
    public Page<SystemLog> findByLevel(LogLevel level, Pageable pageable) {
        return systemLogRepository.findByLevel(level, pageable);
    }

    public Page<SystemLog> findByType(LogType type, Pageable pageable) {
        return systemLogRepository.findByType(type, pageable);
    }

    public Page<SystemLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return systemLogRepository.findByTimestampBetween(startDate, endDate, pageable);
    }


    public Long getLogCount() {
        return systemLogRepository.count();
    }
    
    public Long getLogCountByLevel(String level) {
        try {
            LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
            return systemLogRepository.countByLevel(logLevel);
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }
    
    public Long countByLevelAndDateAfter(LogLevel level, LocalDateTime startDate) {
        return systemLogRepository.countByLevelAndDateAfter(level, startDate);
    }

    public List<Object[]> getLogCountByLevel(LocalDateTime startDate, LocalDateTime endDate) {
        return systemLogRepository.getLogCountByLevel(startDate, endDate);
    }

    public List<Object[]> getLogCountByType(LocalDateTime startDate, LocalDateTime endDate) {
        return systemLogRepository.getLogCountByType(startDate, endDate);
    }

    // API Response metodları
    public ResponseEntity<ApiResponse<List<SystemLog>>> getAllLogs() {
        List<SystemLog> logs = findAll();
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs retrieved successfully"));
    }

    public ResponseEntity<ApiResponse<SystemLog>> getLogById(Long id) {
        SystemLog log = findById(id);
        return ResponseEntity.ok(ApiResponse.success(log, "Log retrieved successfully"));
    }

    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByLevel(LogLevel level) {
        List<SystemLog> logs = findByLevel(level);
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs retrieved successfully"));
    }

    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByType(LogType type) {
        List<SystemLog> logs = findByType(type);
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs retrieved successfully"));
    }

    public ResponseEntity<ApiResponse<List<SystemLog>>> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SystemLog> logs = findByTimestampBetween(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs retrieved successfully"));
    }

    public ResponseEntity<ApiResponse<List<SystemLog>>> getErrorLogs() {
        List<SystemLog> logs = findErrors();
        return ResponseEntity.ok(ApiResponse.success(logs, "Error logs retrieved successfully"));
    }

    public ResponseEntity<ApiResponse<List<SystemLog>>> searchLogs(String keyword) {
        List<SystemLog> logs = findByKeyword(keyword);
        return ResponseEntity.ok(ApiResponse.success(logs, "Logs searched successfully"));
    }
    
    // Log temizleme metodları
    public int deleteOldNonErrorLogs(LocalDateTime cutoffDate) {
        return systemLogRepository.deleteOldNonErrorLogs(cutoffDate);
    }
    
    public Long countOldNonErrorLogs(LocalDateTime cutoffDate) {
        return systemLogRepository.countOldNonErrorLogs(cutoffDate);
    }
    
    public int deleteAllOldLogs(LocalDateTime cutoffDate) {
        return systemLogRepository.deleteAllOldLogs(cutoffDate);
    }
    
    public int deleteOldLogsByLevel(LocalDateTime cutoffDate, LogLevel level) {
        return systemLogRepository.deleteOldLogsByLevel(cutoffDate, level);
    }
    
    public int deleteOldLogsByType(LocalDateTime cutoffDate, LogType type) {
        return systemLogRepository.deleteOldLogsByType(cutoffDate, type);
    }
    
    // 1 ay önceki hata olmayan logları silme
    public int cleanupOldLogs() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return deleteOldNonErrorLogs(oneMonthAgo);
    }
    
    // Check count of logs to be deleted
    public Long getOldLogsCount() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return countOldNonErrorLogs(oneMonthAgo);
    }
    
    // Manuel temizlik için API response
    public ResponseEntity<ApiResponse<String>> cleanupOldLogsResponse() {
        try {
            Long countBefore = getOldLogsCount();
            int deletedCount = cleanupOldLogs();
            
            String message = String.format("Cleanup completed. Deleted %d logs older than 1 month (non-error logs). Total old logs before cleanup: %d", 
                deletedCount, countBefore);
            
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            log.error("Failed to cleanup old logs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to cleanup old logs", "ERR_12006", e.getMessage()));
        }
    }
} 