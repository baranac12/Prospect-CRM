package com.prospect.crm.scheduler;

import com.prospect.crm.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenCleanupScheduler {
    
    private final JwtService jwtService;
    
    public JwtTokenCleanupScheduler(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    
    /**
     * Her saat başı süresi dolmuş tokenları işaretler
     * Cron format: saniye dakika saat gün ay hafta
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanupExpiredTokens() {
        try {
            log.info("Starting JWT token cleanup process...");
            
            jwtService.markExpiredTokens();
            
            log.info("JWT token cleanup completed successfully");
            
        } catch (Exception e) {
            log.error("Error during JWT token cleanup: {}", e.getMessage(), e);
        }
    }
} 