package com.prospect.crm.service;

import com.prospect.crm.model.EmailLog;
import com.prospect.crm.model.EmailDraft;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.EmailLogRepository;
import com.prospect.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailLogService {

    private final EmailLogRepository emailLogRepository;
    private final UserRepository userRepository;
    private final SystemLogService systemLogService;

    /**
     * Email gönderimi için log kaydı oluşturur
     */
    @Transactional
    public void logEmailSent(Long userId, String recipientEmail, String status, String errorMessage, EmailDraft draft) {
        try {
            Users user = userRepository.getReferenceById(userId);
            
            EmailLog emailLog = new EmailLog();
            emailLog.setUserId(user);
            emailLog.setDraftId(draft);
            emailLog.setRecipientEmail(recipientEmail);
            emailLog.setStatus(status);
            emailLog.setResponseReceived(false);
            emailLog.setErrorMessage(errorMessage);
            emailLog.setSentAt(LocalDateTime.now());
            
            emailLogRepository.save(emailLog);
            
            systemLogService.logInfo("Email log created", 
                "User: " + userId + ", Recipient: " + recipientEmail + ", Status: " + status,
                "EmailLogService", "logEmailSent");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to create email log", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "logEmailSent");
            log.error("Error creating email log: {}", e.getMessage(), e);
        }
    }

    /**
     * Email gönderimi için log kaydı oluşturur (draft olmadan)
     */
    @Transactional
    public void logEmailSent(Long userId, String recipientEmail, String status, String errorMessage) {
        logEmailSent(userId, recipientEmail, status, errorMessage, null);
    }

    /**
     * Email okuma işlemi için log kaydı oluşturur
     */
    @Transactional
    public void logEmailRead(Long userId, String emailAddress, String status, String errorMessage) {
        try {
            Users user = userRepository.getReferenceById(userId);
            
            EmailLog emailLog = new EmailLog();
            emailLog.setUserId(user);
            emailLog.setDraftId(null);
            emailLog.setRecipientEmail(emailAddress);
            emailLog.setStatus("READ_" + status);
            emailLog.setResponseReceived(true);
            emailLog.setErrorMessage(errorMessage);
            emailLog.setSentAt(LocalDateTime.now());
            
            emailLogRepository.save(emailLog);
            
            systemLogService.logInfo("Email read log created", 
                "User: " + userId + ", Email: " + emailAddress + ", Status: " + status,
                "EmailLogService", "logEmailRead");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to create email read log", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "logEmailRead");
            log.error("Error creating email read log: {}", e.getMessage(), e);
        }
    }

    /**
     * Email silme işlemi için log kaydı oluşturur
     */
    @Transactional
    public void logEmailDelete(Long userId, String emailAddress, String status, String errorMessage) {
        try {
            Users user = userRepository.getReferenceById(userId);
            
            EmailLog emailLog = new EmailLog();
            emailLog.setUserId(user);
            emailLog.setDraftId(null);
            emailLog.setRecipientEmail(emailAddress);
            emailLog.setStatus("DELETE_" + status);
            emailLog.setResponseReceived(true);
            emailLog.setErrorMessage(errorMessage);
            emailLog.setSentAt(LocalDateTime.now());
            
            emailLogRepository.save(emailLog);
            
            systemLogService.logInfo("Email delete log created", 
                "User: " + userId + ", Email: " + emailAddress + ", Status: " + status,
                "EmailLogService", "logEmailDelete");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to create email delete log", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "logEmailDelete");
            log.error("Error creating email delete log: {}", e.getMessage(), e);
        }
    }

    /**
     * Email listeleme işlemi için log kaydı oluşturur
     */
    @Transactional
    public void logEmailList(Long userId, String emailAddress, String status, String errorMessage) {
        try {
            Users user = userRepository.getReferenceById(userId);
            
            EmailLog emailLog = new EmailLog();
            emailLog.setUserId(user);
            emailLog.setDraftId(null);
            emailLog.setRecipientEmail(emailAddress);
            emailLog.setStatus("LIST_" + status);
            emailLog.setResponseReceived(true);
            emailLog.setErrorMessage(errorMessage);
            emailLog.setSentAt(LocalDateTime.now());
            
            emailLogRepository.save(emailLog);
            
            systemLogService.logInfo("Email list log created", 
                "User: " + userId + ", Email: " + emailAddress + ", Status: " + status,
                "EmailLogService", "logEmailList");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to create email list log", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "logEmailList");
            log.error("Error creating email list log: {}", e.getMessage(), e);
        }
    }

    /**
     * Email etiketleme işlemi için log kaydı oluşturur (mark as read/unread, star/unstar)
     */
    @Transactional
    public void logEmailAction(Long userId, String emailId, String action, String status, String errorMessage) {
        try {
            Users user = userRepository.getReferenceById(userId);
            
            EmailLog emailLog = new EmailLog();
            emailLog.setUserId(user);
            emailLog.setDraftId(null);
            emailLog.setRecipientEmail(emailId); // Using emailId as recipientEmail for action logs
            emailLog.setStatus(action + "_" + status);
            emailLog.setResponseReceived(true);
            emailLog.setErrorMessage(errorMessage);
            emailLog.setSentAt(LocalDateTime.now());
            
            emailLogRepository.save(emailLog);
            
            systemLogService.logInfo("Email action log created", 
                "User: " + userId + ", Email ID: " + emailId + ", Action: " + action + ", Status: " + status,
                "EmailLogService", "logEmailAction");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to create email action log", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "logEmailAction");
            log.error("Error creating email action log: {}", e.getMessage(), e);
        }
    }

    /**
     * Email template rendering işlemi için log kaydı oluşturur
     */
    @Transactional
    public void logEmailTemplateRendering(Long userId, String templateName, String status, String errorMessage) {
        try {
            Users user = userRepository.getReferenceById(userId);
            
            EmailLog emailLog = new EmailLog();
            emailLog.setUserId(user);
            emailLog.setDraftId(null);
            emailLog.setRecipientEmail("TEMPLATE_" + templateName); // Using template name as recipientEmail
            emailLog.setStatus("TEMPLATE_RENDER_" + status);
            emailLog.setResponseReceived(true);
            emailLog.setErrorMessage(errorMessage);
            emailLog.setSentAt(LocalDateTime.now());
            
            emailLogRepository.save(emailLog);
            
            systemLogService.logInfo("Email template rendering log created", 
                "User: " + userId + ", Template: " + templateName + ", Status: " + status,
                "EmailLogService", "logEmailTemplateRendering");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to create email template rendering log", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "logEmailTemplateRendering");
            log.error("Error creating email template rendering log: {}", e.getMessage(), e);
        }
    }

    /**
     * Kullanıcının email loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<EmailLog> getUserEmailLogs(Long userId) {
        try {
            Users user = userRepository.getReferenceById(userId);
            return emailLogRepository.findByUserIdOrderBySentAtDesc(user);
        } catch (Exception e) {
            systemLogService.logError("Failed to get user email logs", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "getUserEmailLogs");
            log.error("Error getting user email logs: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Belirli bir tarih aralığındaki email loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<EmailLog> getEmailLogsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Users user = userRepository.getReferenceById(userId);
            return emailLogRepository.findByUserIdAndSentAtBetweenOrderBySentAtDesc(user, startDate, endDate);
        } catch (Exception e) {
            systemLogService.logError("Failed to get email logs by date range", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "getEmailLogsByDateRange");
            log.error("Error getting email logs by date range: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Belirli bir durumdaki email loglarını getirir
     */
    @Transactional(readOnly = true)
    public List<EmailLog> getEmailLogsByStatus(Long userId, String status) {
        try {
            Users user = userRepository.getReferenceById(userId);
            return emailLogRepository.findByUserIdAndStatusOrderBySentAtDesc(user, status);
        } catch (Exception e) {
            systemLogService.logError("Failed to get email logs by status", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "getEmailLogsByStatus");
            log.error("Error getting email logs by status: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Başarısız email gönderimlerini getirir
     */
    @Transactional(readOnly = true)
    public List<EmailLog> getFailedEmailLogs(Long userId) {
        try {
            Users user = userRepository.getReferenceById(userId);
            return emailLogRepository.findByUserIdAndStatusContainingIgnoreCaseOrderBySentAtDesc(user, "FAILED");
        } catch (Exception e) {
            systemLogService.logError("Failed to get failed email logs", e.getMessage(), e.getStackTrace().toString(),
                "EmailLogService", "getFailedEmailLogs");
            log.error("Error getting failed email logs: {}", e.getMessage(), e);
            return List.of();
        }
    }
} 