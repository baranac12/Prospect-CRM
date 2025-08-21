package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.DailyEmailLimit;
import com.prospect.crm.service.DailyEmailLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/email-limits")
@RequiredArgsConstructor
public class EmailLimitController {

    private final DailyEmailLimitService dailyEmailLimitService;

    // ==================== USER ENDPOINTS ====================

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmailLimitStatus(@RequestParam Long userId) {
        try {
            DailyEmailLimit dailyLimit = dailyEmailLimitService.getDailyLimitInfo(userId);
            Integer remaining = dailyEmailLimitService.getRemainingEmails(userId);
            boolean canSend = dailyEmailLimitService.canSendEmail(userId);

            Map<String, Object> status = new HashMap<>();
            status.put("dailyLimit", dailyLimit.getDailyLimit());
            status.put("sentCount", dailyLimit.getSentCount());
            status.put("remainingEmails", remaining);
            status.put("canSend", canSend);
            status.put("date", dailyLimit.getDate().toString());

            return ResponseEntity.ok(ApiResponse.success(status, "Email limit status retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get email limit status", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get email limit status: " + e.getMessage(), "ERR_7014"));
        }
    }

    @GetMapping("/remaining")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRemainingEmails(@RequestParam Long userId) {
        try {
            Integer remaining = dailyEmailLimitService.getRemainingEmails(userId);
            boolean canSend = dailyEmailLimitService.canSendEmail(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("remainingEmails", remaining);
            result.put("canSend", canSend);

            return ResponseEntity.ok(ApiResponse.success(result, "Remaining emails retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get remaining emails", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get remaining emails: " + e.getMessage(), "ERR_7015"));
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> resetDailyLimit(@RequestParam Long userId) {
        try {
            dailyEmailLimitService.resetDailyLimit(userId);
            return ResponseEntity.ok(ApiResponse.success("Daily limit reset successfully", "Daily email limit reset successfully"));
        } catch (Exception e) {
            log.error("Failed to reset daily limit", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to reset daily limit: " + e.getMessage(), "ERR_7016"));
        }
    }

    @GetMapping("/admin/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmailLimitStatusAdmin(@RequestParam Long userId) {
        try {
            DailyEmailLimit dailyLimit = dailyEmailLimitService.getDailyLimitInfo(userId);
            Integer remaining = dailyEmailLimitService.getRemainingEmails(userId);
            boolean canSend = dailyEmailLimitService.canSendEmail(userId);

            Map<String, Object> status = new HashMap<>();
            status.put("userId", userId);
            status.put("dailyLimit", dailyLimit.getDailyLimit());
            status.put("sentCount", dailyLimit.getSentCount());
            status.put("remainingEmails", remaining);
            status.put("canSend", canSend);
            status.put("date", dailyLimit.getDate().toString());
            status.put("createdAt", dailyLimit.getCreatedAt());
            status.put("updatedAt", dailyLimit.getUpdatedAt());

            return ResponseEntity.ok(ApiResponse.success(status, "Email limit status retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get email limit status", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get email limit status: " + e.getMessage(), "ERR_7014"));
        }
    }
} 