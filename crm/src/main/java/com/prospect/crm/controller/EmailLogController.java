package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.EmailLog;
import com.prospect.crm.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/email-logs")
@RequiredArgsConstructor
public class EmailLogController {

    private final EmailLogService emailLogService;

    /**
     * Kullanıcının email loglarını getirir
     */
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmailLog>>> getUserEmailLogs(
            @RequestParam Long userId) {
        try {
            List<EmailLog> logs = emailLogService.getUserEmailLogs(userId);
            return ResponseEntity.ok(ApiResponse.success(logs, "Email logs retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting user email logs: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get email logs: " + e.getMessage(), "EMAIL_LOG_RETRIEVAL_FAILED"));
        }
    }

    /**
     * Belirli tarih aralığındaki email loglarını getirir
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmailLog>>> getEmailLogsByDateRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<EmailLog> logs = emailLogService.getEmailLogsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(logs, "Email logs retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting email logs by date range: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get email logs: " + e.getMessage(), "EMAIL_LOG_DATE_RANGE_FAILED"));
        }
    }

    /**
     * Belirli durumdaki email loglarını getirir
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmailLog>>> getEmailLogsByStatus(
            @RequestParam Long userId,
            @RequestParam String status) {
        try {
            List<EmailLog> logs = emailLogService.getEmailLogsByStatus(userId, status);
            return ResponseEntity.ok(ApiResponse.success(logs, "Email logs retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting email logs by status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get email logs: " + e.getMessage(), "EMAIL_LOG_STATUS_FAILED"));
        }
    }

    /**
     * Başarısız email gönderimlerini getirir
     */
    @GetMapping("/failed")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmailLog>>> getFailedEmailLogs(
            @RequestParam Long userId) {
        try {
            List<EmailLog> logs = emailLogService.getFailedEmailLogs(userId);
            return ResponseEntity.ok(ApiResponse.success(logs, "Failed email logs retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting failed email logs: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get failed email logs: " + e.getMessage(), "EMAIL_LOG_FAILED_RETRIEVAL_FAILED"));
        }
    }

    /**
     * Email log istatistiklerini getirir
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmailLogStatistics>> getEmailLogStatistics(
            @RequestParam Long userId) {
        try {
            List<EmailLog> allLogs = emailLogService.getUserEmailLogs(userId);
            
            long totalEmails = allLogs.size();
            long successfulEmails = allLogs.stream()
                    .filter(log -> log.getStatus() != null && log.getStatus().contains("SUCCESS"))
                    .count();
            long failedEmails = allLogs.stream()
                    .filter(log -> log.getStatus() != null && log.getStatus().contains("FAILED"))
                    .count();
            long sentEmails = allLogs.stream()
                    .filter(log -> log.getStatus() != null && log.getStatus().contains("SENT"))
                    .count();
            
            EmailLogStatistics statistics = EmailLogStatistics.builder()
                    .totalEmails(totalEmails)
                    .successfulEmails(successfulEmails)
                    .failedEmails(failedEmails)
                    .sentEmails(sentEmails)
                    .successRate(totalEmails > 0 ? (double) successfulEmails / totalEmails * 100 : 0)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success(statistics, "Email log statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting email log statistics: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get email log statistics: " + e.getMessage(), "EMAIL_LOG_STATISTICS_FAILED"));
        }
    }

    // ==================== INNER CLASSES ====================

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class EmailLogStatistics {
        private long totalEmails;
        private long successfulEmails;
        private long failedEmails;
        private long sentEmails;
        private double successRate;
    }
} 