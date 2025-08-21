package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;

import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.StripeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/stripe")
@PreAuthorize("hasRole('USER')")
public class StripeController {
    
    private final StripeService stripeService;
    
    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
    }
    
    /**
     * Stripe checkout session oluşturur
     */
    @PostMapping("/checkout-session")
    @HasPermission(PermissionConstants.SUBSCRIPTION_CREATE)
    public ResponseEntity<ApiResponse<Map<String, String>>> createCheckoutSession(
            @RequestParam String subscriptionCode,
            @RequestParam Long userId) {
        try {
            log.info("Creating Stripe checkout session for subscription: {} and user: {}", subscriptionCode, userId);
            return stripeService.createCheckoutSession(subscriptionCode, userId);
                    
        } catch (Exception e) {
            log.error("Error creating checkout session: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, String>>builder()
                    .success(false)
                    .message("Failed to create checkout session: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Stripe ödeme simülasyonu yapar (test ortamı için)
     */
    @PostMapping("/simulate-payment")
    @HasPermission(PermissionConstants.PAYMENT_CREATE)
    public ResponseEntity<ApiResponse<String>> simulatePayment(
            @RequestParam String sessionId,
            @RequestParam String subscriptionCode,
            @RequestParam Long userId) {
        try {
            log.info("Simulating payment for session: {}, subscription: {}, user: {}", sessionId, subscriptionCode, userId);
            return stripeService.simulatePayment(sessionId, subscriptionCode, userId);
                    
        } catch (Exception e) {
            log.error("Error simulating payment: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to simulate payment: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Stripe webhook'larını işler
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            log.info("Processing Stripe webhook");
            return stripeService.handleWebhook(payload, sigHeader);
                    
        } catch (Exception e) {
            log.error("Error handling Stripe webhook: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to handle webhook: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Müşteri portal oturumu oluşturur
     */
    @PostMapping("/customer-portal")
    @HasPermission(PermissionConstants.SUBSCRIPTION_UPDATE)
    public ResponseEntity<ApiResponse<Map<String, String>>> createCustomerPortalSession(@RequestParam Long userId) {
        try {
            log.info("Creating customer portal session for user: {}", userId);
            return stripeService.createCustomerPortalSession(userId);
                    
        } catch (Exception e) {
            log.error("Error creating customer portal session: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, String>>builder()
                    .success(false)
                    .message("Failed to create customer portal session: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Tüm abonelik tiplerini getirir
     */
    @GetMapping("/subscription-types")
    @HasPermission(PermissionConstants.SUBSCRIPTION_READ)
    public ResponseEntity<ApiResponse<Object>> getAllSubscriptionTypes() {
        try {
            log.info("Retrieving all subscription types from Stripe");
            return stripeService.getAllSubscriptionTypes();
                    
        } catch (Exception e) {
            log.error("Error retrieving subscription types: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve subscription types: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Belirli bir abonelik tipini getirir
     */
    @GetMapping("/subscription-types/{id}")
    @HasPermission(PermissionConstants.SUBSCRIPTION_READ)
    public ResponseEntity<ApiResponse<Object>> getSubscriptionTypeById(@PathVariable Long id) {
        try {
            log.info("Retrieving subscription type with ID: {}", id);
            return stripeService.getSubscriptionTypeById(id);
                    
        } catch (Exception e) {
            log.error("Error retrieving subscription type {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve subscription type: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Stripe abonelik durumunu kontrol eder
     */
    @GetMapping("/subscription-status/{userId}")
    @HasPermission(PermissionConstants.SUBSCRIPTION_READ)
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSubscriptionStatus(@PathVariable Long userId) {
        try {
            log.info("Checking subscription status for user: {}", userId);
            
            // Bu fonksiyonalite StripeService'e eklenebilir
            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Subscription status check - functionality to be implemented")
                    .data(Map.of("userId", userId, "status", "pending_implementation"))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error checking subscription status for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Failed to check subscription status: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Stripe faturalarını listeler
     */
    @GetMapping("/invoices/{userId}")
    @HasPermission(PermissionConstants.PAYMENT_READ)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserInvoices(@PathVariable Long userId) {
        try {
            log.info("Retrieving invoices for user: {}", userId);
            
            // Bu fonksiyonalite StripeService'e eklenebilir
            return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                    .success(true)
                    .message("User invoices - functionality to be implemented")
                    .data(List.of())
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving invoices for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<Map<String, Object>>>builder()
                    .success(false)
                    .message("Failed to retrieve invoices: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Abonelik iptal eder
     */
    @PostMapping("/cancel-subscription")
    @HasPermission(PermissionConstants.SUBSCRIPTION_CANCEL)
    public ResponseEntity<ApiResponse<String>> cancelSubscription(@RequestParam Long userId) {
        try {
            log.info("Cancelling subscription for user: {}", userId);
            
            // Bu fonksiyonalite StripeService'e eklenebilir
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Subscription cancellation - functionality to be implemented")
                    .data("Cancellation pending for user: " + userId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error cancelling subscription for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to cancel subscription: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Abonelik yeniler
     */
    @PostMapping("/renew-subscription")
    @HasPermission(PermissionConstants.SUBSCRIPTION_RENEW)
    public ResponseEntity<ApiResponse<String>> renewSubscription(@RequestParam Long userId) {
        try {
            log.info("Renewing subscription for user: {}", userId);
            
            // Bu fonksiyonalite StripeService'e eklenebilir
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Subscription renewal - functionality to be implemented")
                    .data("Renewal pending for user: " + userId)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error renewing subscription for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to renew subscription: " + e.getMessage())
                    .build());
        }
    }
}