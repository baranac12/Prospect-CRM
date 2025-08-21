package com.prospect.crm.scheduler;

import com.prospect.crm.service.BounceEmailService;
import com.prospect.crm.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BounceEmailProcessingScheduler {

    private final BounceEmailService bounceEmailService;
    private final SystemLogService systemLogService;

    /**
     * Her 30 dakikada bir işlenmemiş bounce email'leri işler
     * Cron: Her 30 dakikada bir çalışır
     */
    @Scheduled(cron = "0 */30 * * * ?")
    @Transactional
    public void processUnprocessedBounces() {
        try {
            bounceEmailService.processAllUnprocessedBounces();
            
            systemLogService.logInfo("Unprocessed bounces processed", 
                "Scheduled bounce email processing completed",
                "BounceEmailProcessingScheduler", "processUnprocessedBounces");
                
            log.info("Scheduled bounce email processing completed");
            
        } catch (Exception e) {
            log.error("Error processing unprocessed bounces", e);
            systemLogService.logError("Failed to process unprocessed bounces", 
                e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailProcessingScheduler", "processUnprocessedBounces");
        }
    }

    /**
     * Her gün gece yarısı bounce email istatistiklerini loglar
     * Cron: Her gün saat 00:00'da çalışır
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(readOnly = true)
    public void logBounceEmailStatistics() {
        try {
            // Bu metod gelecekte bounce email istatistiklerini loglamak için kullanılabilir
            systemLogService.logInfo("Bounce email statistics logging", 
                "Daily bounce email statistics logging completed",
                "BounceEmailProcessingScheduler", "logBounceEmailStatistics");
                
            log.info("Daily bounce email statistics logging completed");
            
        } catch (Exception e) {
            log.error("Error logging bounce email statistics", e);
            systemLogService.logError("Failed to log bounce email statistics", 
                e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailProcessingScheduler", "logBounceEmailStatistics");
        }
    }
} 