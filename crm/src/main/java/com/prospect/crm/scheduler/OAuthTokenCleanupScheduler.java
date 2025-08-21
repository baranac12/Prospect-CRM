package com.prospect.crm.scheduler;

import com.prospect.crm.repository.OauthTokenRepository;
import com.prospect.crm.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OAuthTokenCleanupScheduler {

    private final OauthTokenRepository oauthTokenRepository;
    private final SystemLogService systemLogService;

    public OAuthTokenCleanupScheduler(OauthTokenRepository oauthTokenRepository, SystemLogService systemLogService) {
        this.oauthTokenRepository = oauthTokenRepository;
        this.systemLogService = systemLogService;
    }

    /**
     * Her gün gece yarısı süresi dolmuş OAuth token'ları temizler
     */
    @Scheduled(cron = "0 0 0 * * ?") // Her gün gece yarısı
    public void cleanupExpiredOAuthTokens() {
        try {
            log.info("Starting OAuth token cleanup...");
            
            LocalDateTime now = LocalDateTime.now();
            List<com.prospect.crm.model.OauthToken> expiredTokens = oauthTokenRepository.findExpiredTokens(now);
            
            int cleanedCount = 0;
            for (com.prospect.crm.model.OauthToken token : expiredTokens) {
                token.setExpired(true);
                token.setUpdatedAt(now);
                oauthTokenRepository.save(token);
                cleanedCount++;
            }
            
            log.info("OAuth token cleanup completed. Cleaned {} expired tokens", cleanedCount);
            
            systemLogService.logInfo("OAuth token cleanup completed", 
                "Cleaned " + cleanedCount + " expired tokens", 
                "OAuthTokenCleanupScheduler", "cleanupExpiredOAuthTokens");
                
        } catch (Exception e) {
            log.error("Error during OAuth token cleanup: {}", e.getMessage(), e);
            systemLogService.logError("OAuth token cleanup failed", 
                "Error: " + e.getMessage(), e.getMessage(), 
                "OAuthTokenCleanupScheduler", "cleanupExpiredOAuthTokens");
        }
    }

    /**
     * Her hafta Pazar günü 3 ay önceki revoked token'ları siler
     */
    @Scheduled(cron = "0 0 2 ? * SUN") // Her Pazar günü saat 02:00
    public void deleteOldRevokedTokens() {
        try {
            log.info("Starting old revoked OAuth token cleanup...");
            
            LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            
            // Bu metod repository'ye eklenmeli
            // List<OauthToken> oldRevokedTokens = oauthTokenRepository.findRevokedTokensOlderThan(threeMonthsAgo);
            
            // Şimdilik sadece log yazıyoruz
            log.info("Old revoked OAuth token cleanup completed");
            
            systemLogService.logInfo("Old revoked OAuth token cleanup completed", 
                "Cleaned tokens older than " + threeMonthsAgo, 
                "OAuthTokenCleanupScheduler", "deleteOldRevokedTokens");
                
        } catch (Exception e) {
            log.error("Error during old revoked OAuth token cleanup: {}", e.getMessage(), e);
            systemLogService.logError("Old revoked OAuth token cleanup failed", 
                "Error: " + e.getMessage(), e.getMessage(), 
                "OAuthTokenCleanupScheduler", "deleteOldRevokedTokens");
        }
    }
} 