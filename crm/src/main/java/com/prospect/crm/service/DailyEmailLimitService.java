package com.prospect.crm.service;


import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.DailyEmailLimit;
import com.prospect.crm.model.Users;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.repository.DailyEmailLimitRepository;
import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEmailLimitService {

    private final DailyEmailLimitRepository dailyEmailLimitRepository;
    private final UserRepository userRepository;
    private final UserSubsInfoRepository userSubsInfoRepository;
    private final SystemLogService systemLogService;

    /**
     * Check if user can send email (has remaining daily limit)
     */
    @Transactional(readOnly = true)
    public boolean canSendEmail(Long userId) {
        try {
            LocalDate today = LocalDate.now();

            // Get user's daily limit record for today
            DailyEmailLimit dailyLimit = getOrCreateDailyLimit(userId, today);

            boolean canSend = dailyLimit.getSentCount() < dailyLimit.getDailyLimit();

            if (!canSend) {
                systemLogService.logWarn("Daily email limit reached",
                        "User " + userId + " has reached daily email limit of " + dailyLimit.getDailyLimit(),
                        "DailyEmailLimitService", "canSendEmail");
            }

            return canSend;
        } catch (Exception e) {
            systemLogService.logError("Error checking daily email limit", e.getMessage(), e.getStackTrace().toString(),
                    "DailyEmailLimitService", "canSendEmail");
            return false;
        }
    }

    /**
     * Increment sent email count for user
     */
    @Transactional
    public void incrementSentCount(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            DailyEmailLimit dailyLimit = getOrCreateDailyLimit(userId, today);

            dailyLimit.setSentCount(dailyLimit.getSentCount() + 1);
            dailyLimit.setUpdatedAt(LocalDateTime.now());
            dailyEmailLimitRepository.save(dailyLimit);

            systemLogService.logInfo("Email sent count incremented",
                    "User " + userId + " sent email. New count: " + dailyLimit.getSentCount() + "/" + dailyLimit.getDailyLimit(),
                    "DailyEmailLimitService", "incrementSentCount");

        } catch (Exception e) {
            systemLogService.logError("Error incrementing sent count", e.getMessage(), e.getStackTrace().toString(),
                    "DailyEmailLimitService", "incrementSentCount");
            throw new RuntimeException("Failed to update email sent count");
        }
    }

    /**
     * Get remaining emails for user today
     */
    @Transactional(readOnly = true)
    public Integer getRemainingEmails(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            DailyEmailLimit dailyLimit = getOrCreateDailyLimit(userId, today);

            return Math.max(0, dailyLimit.getDailyLimit() - dailyLimit.getSentCount());
        } catch (Exception e) {
            systemLogService.logError("Error getting remaining emails", e.getMessage(), e.getStackTrace().toString(),
                    "DailyEmailLimitService", "getRemainingEmails");
            return 0;
        }
    }

    /**
     * Get daily limit info for user
     */
    @Transactional(readOnly = true)
    public DailyEmailLimit getDailyLimitInfo(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            return getOrCreateDailyLimit(userId, today);
        } catch (Exception e) {
            systemLogService.logError("Error getting daily limit info", e.getMessage(), e.getStackTrace().toString(),
                    "DailyEmailLimitService", "getDailyLimitInfo");
            throw new RuntimeException("Failed to get daily limit info");
        }
    }

    /**
     * Reset daily limit for user (admin function)
     */
    @Transactional
    public void resetDailyLimit(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            DailyEmailLimit dailyLimit = dailyEmailLimitRepository.findByUserIdIdAndDate(userId, today)
                    .orElse(null);

            if (dailyLimit != null) {
                dailyLimit.setSentCount(0);
                dailyLimit.setUpdatedAt(LocalDateTime.now());
                dailyEmailLimitRepository.save(dailyLimit);

                systemLogService.logInfo("Daily email limit reset",
                        "User " + userId + " daily email limit reset to 0",
                        "DailyEmailLimitService", "resetDailyLimit");
            }
        } catch (Exception e) {
            systemLogService.logError("Error resetting daily limit", e.getMessage(), e.getStackTrace().toString(),
                    "DailyEmailLimitService", "resetDailyLimit");
            throw new RuntimeException("Failed to reset daily limit");
        }
    }

    /**
     * Validate email sending with limit check (without incrementing)
     */
    @Transactional
    public void validateEmailLimit(Long userId) {
        if (!canSendEmail(userId)) {
            Integer remaining = getRemainingEmails(userId);
            throw new ValidationException("Daily email limit reached. Remaining emails: " + remaining);
        }
    }

    /**
     * Increment sent count for successful email sending
     */
    @Transactional
    public void incrementSuccessfulEmailCount(Long userId) {
        incrementSentCount(userId);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Get or create daily limit record for user and date
     */
    private DailyEmailLimit getOrCreateDailyLimit(Long userId, LocalDate date) {
        return dailyEmailLimitRepository.findByUserIdIdAndDate(userId, date)
                .orElseGet(() -> createDailyLimit(userId, date));
    }

    /**
     * Create new daily limit record for user
     */
    private DailyEmailLimit createDailyLimit(Long userId, LocalDate date) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.USER_NOT_FOUND + " :" + userId));
            // Get user's subscription info to determine daily limit
            UserSubsInfo userSubsInfo = userSubsInfoRepository.findByUsersIdAndIsActiveTrue(user)
                    .orElseThrow(() -> new ValidationException("No active subscription found for user: " + userId));

            // Get daily limit from subscription type
            Integer dailyLimit = userSubsInfo.getSubscriptionTypeId().getDailyLimit();
            if (dailyLimit == null || dailyLimit <= 0) {
                dailyLimit = 10; // Default limit
            }

            DailyEmailLimit newLimit = new DailyEmailLimit();
            newLimit.setUserId(user);
            newLimit.setDate(date);
            newLimit.setSentCount(0);
            newLimit.setDailyLimit(dailyLimit);
            newLimit.setCreatedAt(LocalDateTime.now());
            newLimit.setUpdatedAt(LocalDateTime.now());

            DailyEmailLimit saved = dailyEmailLimitRepository.save(newLimit);

            systemLogService.logInfo("Daily email limit created",
                    "User " + userId + " daily limit set to " + dailyLimit + " for date " + date,
                    "DailyEmailLimitService", "createDailyLimit");

            return saved;
        } catch (Exception e) {
            systemLogService.logError("Error creating daily limit", e.getMessage(), e.getStackTrace().toString(),
                    "DailyEmailLimitService", "createDailyLimit");
            throw new RuntimeException("Failed to create daily limit record");
        }
    }
} 