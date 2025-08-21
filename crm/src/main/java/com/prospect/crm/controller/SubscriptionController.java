package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/subscriptions")
@PreAuthorize("hasRole('USER')")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Kullanıcının abonelik durumunu getir
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubscriptionStatus(@RequestParam Long userId) {
        try {
            Map<String, Object> status = subscriptionService.checkSubscriptionStatus(userId);
            return ResponseEntity.ok(ApiResponse.success(status, "Subscription status retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting subscription status for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get subscription status", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Grace period'da olan kullanıcıları getir (admin için)
     */
    @GetMapping("/grace-period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserSubsInfo>>> getUsersInGracePeriod() {
        try {
            List<UserSubsInfo> users = subscriptionService.getUsersInGracePeriod();
            return ResponseEntity.ok(ApiResponse.success(users, "Users in grace period retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting users in grace period: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get users in grace period", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Grace period'ı biten kullanıcıları getir (admin için)
     */
    @GetMapping("/expired-grace-period")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserSubsInfo>>> getUsersWithExpiredGracePeriod() {
        try {
            List<UserSubsInfo> users = subscriptionService.getUsersWithExpiredGracePeriod();
            return ResponseEntity.ok(ApiResponse.success(users, "Users with expired grace period retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting users with expired grace period: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get users with expired grace period", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Abonelik süresini uzat (admin için)
     */
    @PostMapping("/extend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> extendSubscription(
            @RequestParam Long userId,
            @RequestParam Integer days) {
        try {
            subscriptionService.extendSubscription(userId, days);
            return ResponseEntity.ok(ApiResponse.success("Subscription extended successfully"));
        } catch (Exception e) {
            log.error("Error extending subscription for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to extend subscription", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Aboneliği iptal et (admin için)
     */
    @PostMapping("/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> cancelSubscription(@RequestParam Long userId) {
        try {
            subscriptionService.cancelSubscription(userId);
            return ResponseEntity.ok(ApiResponse.success("Subscription cancelled successfully"));
        } catch (Exception e) {
            log.error("Error cancelling subscription for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to cancel subscription", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Abonelik yükseltme/düşürme
     */
    @PostMapping("/change-plan")
    public ResponseEntity<ApiResponse<String>> changeSubscriptionPlan(
            @RequestParam Long userId,
            @RequestParam String newPlanCode) {
        try {
            // Bu endpoint Stripe ile entegre edilecek
            // Şimdilik sadece log yazıyoruz
            log.info("Subscription plan change requested for user: {}, new plan: {}", userId, newPlanCode);
            
            return ResponseEntity.ok(ApiResponse.success("Plan change request submitted"));
            
        } catch (Exception e) {
            log.error("Error changing subscription plan for user {}: {}", userId, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to change subscription plan", "ERR_1006", e.getMessage()));
        }
    }
} 