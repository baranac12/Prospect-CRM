package com.prospect.crm.scheduler;

import com.prospect.crm.repository.DailyEmailLimitRepository;
import com.prospect.crm.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyEmailLimitCleanupScheduler {

    private final DailyEmailLimitRepository dailyEmailLimitRepository;
    private final SystemLogService systemLogService;

    /**
     * Her gün gece yarısı eski günlük limit kayıtlarını temizler (30 günden eski)
     * Cron: Her gün saat 00:00'da çalışır
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupOldDailyEmailLimits() {
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(30);
            
            // 30 günden eski kayıtları sil
            long deletedCount = dailyEmailLimitRepository.deleteByDateBefore(cutoffDate);
            
            if (deletedCount > 0) {
                systemLogService.logInfo("Old daily email limits cleaned up", 
                    "Deleted " + deletedCount + " old daily email limit records older than " + cutoffDate,
                    "DailyEmailLimitCleanupScheduler", "cleanupOldDailyEmailLimits");
                
                log.info("Cleaned up {} old daily email limit records", deletedCount);
            } else {
                log.info("No old daily email limit records to clean up");
            }
            
        } catch (Exception e) {
            log.error("Error cleaning up old daily email limits", e);
            systemLogService.logError("Failed to cleanup old daily email limits", 
                e.getMessage(), e.getStackTrace().toString(),
                "DailyEmailLimitCleanupScheduler", "cleanupOldDailyEmailLimits");
        }
    }

    /**
     * Her gün sabah 6'da günlük limit istatistiklerini loglar
     * Cron: Her gün saat 06:00'da çalışır
     */
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional(readOnly = true)
    public void logDailyEmailLimitStatistics() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            // Dünün istatistiklerini al
            long totalRecords = dailyEmailLimitRepository.countByDate(yesterday);
            long usersWithLimits = dailyEmailLimitRepository.countDistinctUserIdByDate(yesterday);
            long totalEmailsSent = dailyEmailLimitRepository.sumSentCountByDate(yesterday);
            
            systemLogService.logInfo("Daily email limit statistics", 
                "Date: " + yesterday + ", Total records: " + totalRecords + 
                ", Users with limits: " + usersWithLimits + 
                ", Total emails sent: " + totalEmailsSent,
                "DailyEmailLimitCleanupScheduler", "logDailyEmailLimitStatistics");
                
            log.info("Daily email limit statistics for {}: {} records, {} users, {} emails sent", 
                yesterday, totalRecords, usersWithLimits, totalEmailsSent);
                
        } catch (Exception e) {
            log.error("Error logging daily email limit statistics", e);
            systemLogService.logError("Failed to log daily email limit statistics", 
                e.getMessage(), e.getStackTrace().toString(),
                "DailyEmailLimitCleanupScheduler", "logDailyEmailLimitStatistics");
        }
    }
} 