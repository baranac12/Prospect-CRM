package com.prospect.crm.service;

import com.prospect.crm.constant.SubscriptionStatus;
import com.prospect.crm.model.SubscriptionType;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SubscriptionService {

    private final UserSubsInfoRepository userSubsInfoRepository;
    private final UserRepository userRepository;

    private static final int GRACE_PERIOD_DAYS = 3;

    public SubscriptionService(UserSubsInfoRepository userSubsInfoRepository, UserRepository userRepository) {
        this.userSubsInfoRepository = userSubsInfoRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> checkSubscriptionStatus(Long userId) {
        Map<String, Object> status = new HashMap<>();
        Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));

        try {
            Optional<UserSubsInfo> subscriptionOpt = userSubsInfoRepository.findByUsersIdAndIsActiveTrue(user);

            if (subscriptionOpt.isEmpty()) {
                status.put("hasSubscription", false);
                status.put("status", SubscriptionStatus.NO_SUBSCRIPTION.getCode());
                status.put("message", "No subscription found");
                status.put("canAccess", false);
                status.put("allowedEndpoints", new String[]{"POST /v1/payments/create-checkout-session", "GET /v1/payments/subscription-types"});
                return status;
            }

            UserSubsInfo subscription = subscriptionOpt.get();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = subscription.getSubsEndDate();
            LocalDateTime gracePeriodEnd = endDate.plusDays(GRACE_PERIOD_DAYS);

            SubscriptionType subscriptionType = subscription.getSubscriptionTypeId();
            boolean isTrial = subscriptionType != null && "trial".equals(subscriptionType.getCode());

            if (endDate.isAfter(now)) {
                long daysRemaining = java.time.Duration.between(now, endDate).toDays();
                status.put("hasSubscription", true);
                status.put("status", SubscriptionStatus.ACTIVE.getCode());
                status.put("message", "Active subscription");
                status.put("daysRemaining", daysRemaining);
                status.put("canAccess", true);
                status.put("allowedEndpoints", new String[]{"*"});
                status.put("subscriptionType", subscriptionType != null ? subscriptionType.getName() : "Unknown");

            } else if (gracePeriodEnd.isAfter(now)) {
                long graceDaysRemaining = java.time.Duration.between(now, gracePeriodEnd).toDays();
                status.put("hasSubscription", true);
                status.put("status", SubscriptionStatus.GRACE_PERIOD.getCode());
                status.put("message", "Subscription expired. You have " + graceDaysRemaining + " days of grace period remaining.");
                status.put("graceDaysRemaining", graceDaysRemaining);
                status.put("canAccess", true);
                status.put("allowedEndpoints", new String[]{"*"});
                status.put("subscriptionType", subscriptionType != null ? subscriptionType.getName() : "Unknown");

            } else {
                if (isTrial) {
                    status.put("hasSubscription", false);
                    status.put("status", SubscriptionStatus.TRIAL_EXPIRED.getCode());
                    status.put("message", "Trial period expired. Please purchase a subscription.");
                    status.put("canAccess", false);
                    status.put("allowedEndpoints", new String[]{
                            "POST /v1/payments/create-checkout-session",
                            "GET /v1/payments/subscription-types",
                            "GET /v1/auth/**",
                            "POST /v1/users/register"
                    });
                } else {
                    status.put("hasSubscription", false);
                    status.put("status", SubscriptionStatus.EXPIRED.getCode());
                    status.put("message", "Subscription expired. Please renew.");
                    status.put("canAccess", false);
                    status.put("allowedEndpoints", new String[]{
                            "POST /v1/payments/create-checkout-session",
                            "GET /v1/payments/subscription-types",
                            "GET /v1/auth/**",
                            "POST /v1/users/register"
                    });
                }
            }

            status.put("startDate", subscription.getSubsStartDate());
            status.put("endDate", subscription.getSubsEndDate());
            status.put("gracePeriodEnd", endDate.plusDays(GRACE_PERIOD_DAYS));

        } catch (Exception e) {
            log.error("Error checking subscription status for user {}: {}", userId, e.getMessage());
            status.put("hasSubscription", false);
            status.put("status", "ERROR");
            status.put("message", "Could not check subscription status");
            status.put("canAccess", false);
            status.put("allowedEndpoints", new String[]{});
        }

        return status;
    }

    public boolean hasValidSubscription(Long userId) {
        try {
            Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));
            Optional<UserSubsInfo> subscriptionOpt = userSubsInfoRepository.findByUsersIdAndIsActiveTrue(user);

            if (subscriptionOpt.isEmpty()) {
                return false;
            }

            UserSubsInfo subscription = subscriptionOpt.get();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = subscription.getSubsEndDate();
            LocalDateTime gracePeriodEnd = endDate.plusDays(GRACE_PERIOD_DAYS);

            return endDate.isAfter(now) || gracePeriodEnd.isAfter(now);

        } catch (Exception e) {
            log.error("Error checking subscription validity for user {}: {}", userId, e.getMessage());
            return false;
        }
    }

    public java.util.List<UserSubsInfo> getUsersInGracePeriod() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodStart = now.minusDays(GRACE_PERIOD_DAYS);

        return userSubsInfoRepository.findAll().stream()
                .filter(sub -> sub.getIsActive())
                .filter(sub -> {
                    LocalDateTime endDate = sub.getSubsEndDate();
                    return endDate.isBefore(now) && endDate.isAfter(gracePeriodStart);
                })
                .toList();
    }

    public java.util.List<UserSubsInfo> getUsersWithExpiredGracePeriod() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gracePeriodEnd = now.minusDays(GRACE_PERIOD_DAYS);

        return userSubsInfoRepository.findAll().stream()
                .filter(sub -> sub.getIsActive())
                .filter(sub -> sub.getSubsEndDate().isBefore(gracePeriodEnd))
                .toList();
    }

    public void extendSubscription(Long userId, int days) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));
        Optional<UserSubsInfo> subscriptionOpt = userSubsInfoRepository.findByUsersIdAndIsActiveTrue(user);

        if (subscriptionOpt.isPresent()) {
            UserSubsInfo subscription = subscriptionOpt.get();
            subscription.setSubsEndDate(subscription.getSubsEndDate().plusDays(days));
            subscription.setUpdatedAt(LocalDateTime.now());
            userSubsInfoRepository.save(subscription);

            log.info("Subscription extended for user {} by {} days", userId, days);
        }
    }

    public void cancelSubscription(Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found" + userId));
        Optional<UserSubsInfo> subscriptionOpt = userSubsInfoRepository.findByUsersIdAndIsActiveTrue(user);
        if (subscriptionOpt.isPresent()) {
            UserSubsInfo subscription = subscriptionOpt.get();
            subscription.setIsActive(false);
            subscription.setUpdatedAt(LocalDateTime.now());
            userSubsInfoRepository.save(subscription);

            log.info("Subscription cancelled for user {}", userId);
        }
    }
} 