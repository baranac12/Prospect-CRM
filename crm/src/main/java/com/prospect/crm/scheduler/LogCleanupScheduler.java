package com.prospect.crm.scheduler;

import com.prospect.crm.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogCleanupScheduler {
    
    private final SystemLogService systemLogService;
    
    public LogCleanupScheduler(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }
    
    /**
     * Her gün saat 02:00'de 1 ay önceki hata olmayan logları temizler
     * Cron format: saniye dakika saat gün ay hafta
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldLogs() {
        try {
            log.info("Starting automatic log cleanup process...");
            
            // Silinecek log sayısını kontrol et
            Long countBefore = systemLogService.getOldLogsCount();
            
            if (countBefore > 0) {
                // Logları temizle
                int deletedCount = systemLogService.cleanupOldLogs();
                
                log.info("Log cleanup completed successfully. Deleted {} logs older than 1 month (non-error logs). Total old logs before cleanup: {}", 
                    deletedCount, countBefore);
                
                // Temizlik sonrası bilgi logu
                systemLogService.logInfo(
                    "Automatic log cleanup completed",
                    String.format("Deleted %d logs older than 1 month. Total old logs before cleanup: %d", deletedCount, countBefore),
                    "LogCleanupScheduler",
                    "cleanupOldLogs"
                );
            } else {
                log.info("No old logs to cleanup. All logs are within 1 month or are error logs.");
            }
            
        } catch (Exception e) {
            log.error("Failed to cleanup old logs: {}", e.getMessage(), e);
            
            // Hata logu
            systemLogService.logError(
                "Automatic log cleanup failed",
                "Failed to cleanup old logs: " + e.getMessage(),
                e.getStackTrace().toString(),
                "LogCleanupScheduler",
                "cleanupOldLogs"
            );
        }
    }
    
    /**
     * Her hafta Pazar günü saat 03:00'de detaylı log analizi yapar
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void weeklyLogAnalysis() {
        try {
            log.info("Starting weekly log analysis...");
            
            // Haftalık log istatistikleri
            Long totalLogs = systemLogService.countByLevelAndDateAfter(
                com.prospect.crm.constant.LogLevel.INFO, 
                java.time.LocalDateTime.now().minusWeeks(1)
            );
            
            Long errorLogs = systemLogService.countByLevelAndDateAfter(
                com.prospect.crm.constant.LogLevel.ERROR, 
                java.time.LocalDateTime.now().minusWeeks(1)
            );
            
            log.info("Weekly log analysis completed. Total logs: {}, Error logs: {}", totalLogs, errorLogs);
            
            // Analiz sonucu logu
            systemLogService.logInfo(
                "Weekly log analysis completed",
                String.format("Total logs: %d, Error logs: %d", totalLogs, errorLogs),
                "LogCleanupScheduler",
                "weeklyLogAnalysis"
            );
            
        } catch (Exception e) {
            log.error("Failed to perform weekly log analysis: {}", e.getMessage(), e);
            
            systemLogService.logError(
                "Weekly log analysis failed",
                "Failed to perform weekly log analysis: " + e.getMessage(),
                e.getStackTrace().toString(),
                "LogCleanupScheduler",
                "weeklyLogAnalysis"
            );
        }
    }
    
    /**
     * Her ayın 1'i saat 04:00'de aylık log raporu oluşturur
     */
    @Scheduled(cron = "0 0 4 1 * ?")
    public void monthlyLogReport() {
        try {
            log.info("Starting monthly log report generation...");
            
            // Aylık log istatistikleri
            Long totalLogs = systemLogService.countByLevelAndDateAfter(
                com.prospect.crm.constant.LogLevel.INFO, 
                java.time.LocalDateTime.now().minusMonths(1)
            );
            
            Long errorLogs = systemLogService.countByLevelAndDateAfter(
                com.prospect.crm.constant.LogLevel.ERROR, 
                java.time.LocalDateTime.now().minusMonths(1)
            );
            
            Long warningLogs = systemLogService.countByLevelAndDateAfter(
                com.prospect.crm.constant.LogLevel.WARN, 
                java.time.LocalDateTime.now().minusMonths(1)
            );
            
            log.info("Monthly log report completed. Total logs: {}, Error logs: {}, Warning logs: {}", 
                totalLogs, errorLogs, warningLogs);
            
            // Rapor sonucu logu
            systemLogService.logInfo(
                "Monthly log report completed",
                String.format("Total logs: %d, Error logs: %d, Warning logs: %d", totalLogs, errorLogs, warningLogs),
                "LogCleanupScheduler",
                "monthlyLogReport"
            );
            
        } catch (Exception e) {
            log.error("Failed to generate monthly log report: {}", e.getMessage(), e);
            
            systemLogService.logError(
                "Monthly log report generation failed",
                "Failed to generate monthly log report: " + e.getMessage(),
                e.getStackTrace().toString(),
                "LogCleanupScheduler",
                "monthlyLogReport"
            );
        }
    }
} 