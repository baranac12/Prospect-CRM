package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.service.StripeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/payments")
@PreAuthorize("hasRole('USER')")
public class PaymentController {

    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    /**
     * Checkout session oluşturur
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<ApiResponse<Map<String, String>>> createCheckoutSession(
            @RequestParam String subscriptionCode,
            @RequestParam Long userId) {
        return stripeService.createCheckoutSession(subscriptionCode, userId);
    }

    /**
     * Ödeme simülasyonu (test amaçlı)
     */
    @PostMapping("/simulate-payment")
    public ResponseEntity<ApiResponse<String>> simulatePayment(
            @RequestParam String sessionId,
            @RequestParam String subscriptionCode,
            @RequestParam Long userId) {
        return stripeService.simulatePayment(sessionId, subscriptionCode, userId);
    }

    /**
     * Müşteri portal URL'i oluşturur
     */
    @PostMapping("/create-portal-session")
    public ResponseEntity<ApiResponse<Map<String, String>>> createCustomerPortalSession(
            @RequestParam Long userId) {
        return stripeService.createCustomerPortalSession(userId);
    }

    /**
     * Webhook endpoint'i
     */
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        return stripeService.handleWebhook(payload, sigHeader);
    }

    /**
     * Abonelik tiplerini listeler
     */
    @GetMapping("/subscription-types")
    public ResponseEntity<ApiResponse<Object>> getSubscriptionTypes() {
        return stripeService.getAllSubscriptionTypes();
    }

    /**
     * Belirli bir abonelik tipini getirir
     */
    @GetMapping("/subscription-types/{id}")
    public ResponseEntity<ApiResponse<Object>> getSubscriptionTypeById(@PathVariable Long id) {
        return stripeService.getSubscriptionTypeById(id);
    }

    /**
     * Ödeme başarılı callback
     */
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> paymentSuccess(
            @RequestParam String session_id,
            @RequestParam(required = false) String subscription_code,
            @RequestParam(required = false) String user_id) {
        
        log.info("Payment success callback - Session: {}, Subscription: {}, User: {}", 
                session_id, subscription_code, user_id);
        
        return ResponseEntity.ok(ApiResponse.success("Payment completed successfully"));
    }

    /**
     * Ödeme iptal callback
     */
    @GetMapping("/cancel")
    public ResponseEntity<ApiResponse<String>> paymentCancel(
            @RequestParam(required = false) String session_id) {
        
        log.info("Payment cancelled - Session: {}", session_id);
        
        return ResponseEntity.ok(ApiResponse.success("Payment was cancelled"));
    }
} 