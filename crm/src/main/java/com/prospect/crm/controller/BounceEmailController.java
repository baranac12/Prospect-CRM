package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.service.BounceEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/bounce-emails")
@RequiredArgsConstructor
public class BounceEmailController {

    private final BounceEmailService bounceEmailService;

    // ==================== ADMIN ENDPOINTS ====================

    @PostMapping("/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> processBounceEmail(
            @RequestParam String emailAddress,
            @RequestParam String bounceType,
            @RequestParam String bounceReason,
            @RequestParam String originalMessageId,
            @RequestParam String provider,
            @RequestParam Long userId) {
        try {
            bounceEmailService.processBounceEmail(emailAddress, bounceType, bounceReason, 
                                                originalMessageId, provider, userId);
            return ResponseEntity.ok(ApiResponse.success("Bounce email processed successfully", "Bounce email processed successfully"));
        } catch (Exception e) {
            log.error("Failed to process bounce email", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process bounce email: " + e.getMessage(), "ERR_7019"));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BounceEmailService.BounceStatistics>> getBounceStatistics(
            @RequestParam String emailAddress) {
        try {
            BounceEmailService.BounceStatistics statistics = bounceEmailService.getBounceStatistics(emailAddress);
            return ResponseEntity.ok(ApiResponse.success(statistics, "Bounce statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get bounce statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get bounce statistics: " + e.getMessage(), "ERR_7020"));
        }
    }

    @PostMapping("/process-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> processAllUnprocessedBounces() {
        try {
            bounceEmailService.processAllUnprocessedBounces();
            return ResponseEntity.ok(ApiResponse.success("All unprocessed bounces processed successfully", "All unprocessed bounces processed successfully"));
        } catch (Exception e) {
            log.error("Failed to process all unprocessed bounces", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to process all unprocessed bounces: " + e.getMessage(), "ERR_7021"));
        }
    }

    @GetMapping("/check")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkEmailBounces(
            @RequestParam String emailAddress) {
        try {
            boolean hasRecentHardBounces = bounceEmailService.hasRecentHardBounces(emailAddress);
            
            Map<String, Object> result = new HashMap<>();
            result.put("emailAddress", emailAddress);
            result.put("hasRecentHardBounces", hasRecentHardBounces);
            result.put("isValidForSending", !hasRecentHardBounces);
            
            return ResponseEntity.ok(ApiResponse.success(result, "Email bounce check completed successfully"));
        } catch (Exception e) {
            log.error("Failed to check email bounces", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to check email bounces: " + e.getMessage(), "ERR_7022"));
        }
    }
} 