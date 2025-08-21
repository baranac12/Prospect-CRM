package com.prospect.crm.service;

import com.prospect.crm.config.StripeConfig;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.SubscriptionType;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.SubscriptionTypeRepository;
import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class StripeService {

    private final StripeConfig stripeConfig;
    private final SubscriptionTypeRepository subscriptionTypeRepository;
    private final UserSubsInfoRepository userSubsInfoRepository;
    private final UserRepository userRepository;

    public StripeService(StripeConfig stripeConfig,
                         SubscriptionTypeRepository subscriptionTypeRepository,
                         UserSubsInfoRepository userSubsInfoRepository, UserRepository userRepository) {
        this.stripeConfig = stripeConfig;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
        this.userSubsInfoRepository = userSubsInfoRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates checkout session (simulated)
     */
    public ResponseEntity<ApiResponse<Map<String, String>>> createCheckoutSession(String subscriptionCode, Long userId) {
        try {
            // Validate subscription type
            subscriptionTypeRepository.findByCodeAndIsActiveTrue(subscriptionCode)
                    .orElseThrow(() -> new RuntimeException("Subscription type not found: " + subscriptionCode));

            // Simüle edilmiş checkout session
            Map<String, String> response = new HashMap<>();
            response.put("sessionId", "cs_" + System.currentTimeMillis());
            response.put("url", stripeConfig.getSuccessUrl() + "?session_id=cs_" + System.currentTimeMillis());
            response.put("subscriptionCode", subscriptionCode);
            response.put("userId", userId.toString());

            log.info("Checkout session created for user: {}, subscription: {}", userId, subscriptionCode);

            return ResponseEntity.ok(ApiResponse.success(response, "Checkout session created successfully"));

        } catch (Exception e) {
            log.error("Error creating checkout session: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to create checkout session", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Payment simulation
     */
    public ResponseEntity<ApiResponse<String>> simulatePayment(String sessionId, String subscriptionCode, Long userId) {
        try {
            // Find subscription type
            SubscriptionType subscriptionType = subscriptionTypeRepository.findByCodeAndIsActiveTrue(subscriptionCode)
                    .orElseThrow(() -> new RuntimeException("Subscription type not found: " + subscriptionCode));

            // Find user
            Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));

            // Deactivate existing subscription
            Optional<UserSubsInfo> existingSubscription = userSubsInfoRepository.findByUsersIdAndIsActiveTrue(user);
            if (existingSubscription.isPresent()) {
                UserSubsInfo existing = existingSubscription.get();
                existing.setIsActive(false);
                userSubsInfoRepository.save(existing);
            }

            // Create new subscription
            UserSubsInfo newSubscription = new UserSubsInfo();
            newSubscription.setUsersId(user);
            newSubscription.setSubscriptionTypeId(subscriptionType);
            newSubscription.setSubsStartDate(LocalDateTime.now());
            newSubscription.setSubsEndDate(LocalDateTime.now().plusDays(subscriptionType.getDurationInDays()));
            newSubscription.setIsActive(true);
            newSubscription.setCreatedAt(LocalDateTime.now());

            userSubsInfoRepository.save(newSubscription);

            log.info("Payment simulated successfully for user: {}, subscription: {}", userId, subscriptionType.getName());

            return ResponseEntity.ok(ApiResponse.success("Payment processed successfully"));

        } catch (Exception e) {
            log.error("Error simulating payment: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to process payment", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Handles webhook events (simulated)
     */
    public ResponseEntity<ApiResponse<String>> handleWebhook(String payload, String sigHeader) {
        try {
            // Webhook validation simulation
            log.info("Webhook received: {}", payload);
            
            // Real Stripe webhook processing code will be here
            // For now, we just log

            return ResponseEntity.ok(ApiResponse.success("Webhook processed successfully"));

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Webhook processing failed", "WEBHOOK_ERROR", e.getMessage()));
        }
    }

    /**
     * Creates customer portal URL (simulated)
     */
    public ResponseEntity<ApiResponse<Map<String, String>>> createCustomerPortalSession(Long userId) {
        try {
            Map<String, String> response = new HashMap<>();
            response.put("url", "http://localhost:3000/account?user_id=" + userId);

            return ResponseEntity.ok(ApiResponse.success(response, "Customer portal session created"));

        } catch (Exception e) {
            log.error("Error creating customer portal session: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to create customer portal session", "STRIPE_ERROR", e.getMessage()));
        }
    }

    /**
     * Gets all subscription types
     */
    public ResponseEntity<ApiResponse<Object>> getAllSubscriptionTypes() {
        try {
            List<SubscriptionType> types = subscriptionTypeRepository.findByIsActiveTrue();
            return ResponseEntity.ok(ApiResponse.success(types, "Subscription types retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting subscription types: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get subscription types", "ERR_1006", e.getMessage()));
        }
    }

    /**
     * Gets subscription type by ID
     */
    public ResponseEntity<ApiResponse<Object>> getSubscriptionTypeById(Long id) {
        try {
            SubscriptionType type = subscriptionTypeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Subscription type not found: " + id));
            return ResponseEntity.ok(ApiResponse.success(type, "Subscription type retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting subscription type: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get subscription type", "ERR_1006", e.getMessage()));
        }
    }
} 