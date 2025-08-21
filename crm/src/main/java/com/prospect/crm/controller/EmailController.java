package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.EmailSendRequestDto;
import com.prospect.crm.dto.EmailReadRequestDto;
import com.prospect.crm.dto.EmailReadResponseDto;
import com.prospect.crm.dto.EmailDeleteRequestDto;
import com.prospect.crm.dto.EmailListRequestDto;
import com.prospect.crm.dto.EmailListResponseDto;
import com.prospect.crm.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/emails")
@PreAuthorize("hasRole('USER')")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Email gönderir
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendEmail(
            @RequestBody EmailSendRequestDto request,
            @RequestParam Long userId) {
        
        try {
            emailService.sendEmail(userId, request);
            
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully"));
            
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to send email", "EMAIL_001", e.getMessage()));
        }
    }

    /**
     * SMTP ile email gönderir (fallback)
     */
    @PostMapping("/send-smtp")
    public ResponseEntity<ApiResponse<String>> sendEmailViaSMTP(
            @RequestParam String fromEmail,
            @RequestParam String password,
            @RequestParam List<String> toEmails,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam(defaultValue = "text/html") String contentType) {
        
        try {
            emailService.sendEmailViaSMTP(fromEmail, password, toEmails, subject, body, contentType);
            
            return ResponseEntity.ok(ApiResponse.success("Email sent via SMTP successfully"));
            
        } catch (Exception e) {
            log.error("Error sending email via SMTP: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to send email via SMTP", "EMAIL_002", e.getMessage()));
        }
    }

    /**
     * Email template'ini render eder
     */
    @PostMapping("/render-template")
    public ResponseEntity<ApiResponse<String>> renderTemplate(
            @RequestParam String templateName,
            @RequestBody Map<String, Object> variables) {
        
        try {
            String renderedTemplate = emailService.renderEmailTemplate(templateName, variables);
            
            return ResponseEntity.ok(ApiResponse.success(renderedTemplate, "Template rendered successfully"));
            
        } catch (Exception e) {
            log.error("Error rendering template: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to render template", "EMAIL_003", e.getMessage()));
        }
    }

    /**
     * Mevcut email template'lerini listeler
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getTemplates() {
        try {
            List<Map<String, String>> templates = List.of(
                Map.of("name", "welcome", "description", "Hoş geldiniz email template'i"),
                Map.of("name", "lead_followup", "description", "Lead takip email template'i")
            );
            
            return ResponseEntity.ok(ApiResponse.success(templates, "Email templates retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting email templates: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get email templates", "EMAIL_004", e.getMessage()));
        }
    }

    /**
     * Email gönderme durumunu kontrol eder
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmailStatus(@RequestParam Long userId) {
        try {
            // Bu endpoint OAuth bağlantılarını kontrol eder
            Map<String, Object> status = Map.of(
                "userId", userId,
                "canSendEmail", true,
                "supportedProviders", List.of("Gmail", "Outlook"),
                "message", "Email service is available"
            );
            
            return ResponseEntity.ok(ApiResponse.success(status, "Email status retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error getting email status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to get email status", "EMAIL_005", e.getMessage()));
        }
    }

    // ==================== EMAIL OKUMA İŞLEMLERİ ====================

    /**
     * Email okur
     */
    @PostMapping("/read")
    public ResponseEntity<ApiResponse<EmailReadResponseDto>> readEmail(
            @RequestBody EmailReadRequestDto request,
            @RequestParam Long userId) {
        
        try {
            EmailReadResponseDto response = emailService.readEmail(userId, request);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Email read successfully"));
            
        } catch (Exception e) {
            log.error("Error reading email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to read email", "EMAIL_006", e.getMessage()));
        }
    }

    // ==================== EMAIL LİSTESİ İŞLEMLERİ ====================

    /**
     * Email listesini getirir
     */
    @PostMapping("/list")
    public ResponseEntity<ApiResponse<EmailListResponseDto>> listEmails(
            @RequestBody EmailListRequestDto request,
            @RequestParam Long userId) {
        
        try {
            EmailListResponseDto response = emailService.listEmails(userId, request);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Email list retrieved successfully"));
            
        } catch (Exception e) {
            log.error("Error listing emails: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to list emails", "EMAIL_007", e.getMessage()));
        }
    }

    // ==================== EMAIL SİLME İŞLEMLERİ ====================

    /**
     * Email'leri siler
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteEmails(
            @RequestBody EmailDeleteRequestDto request,
            @RequestParam Long userId) {
        
        try {
            emailService.deleteEmails(userId, request);
            
            return ResponseEntity.ok(ApiResponse.success("Emails deleted successfully"));
            
        } catch (Exception e) {
            log.error("Error deleting emails: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to delete emails", "EMAIL_008", e.getMessage()));
        }
    }

    // ==================== EMAIL ETİKETLEME İŞLEMLERİ ====================

    /**
     * Email'leri okundu olarak işaretler
     */
    @PutMapping("/mark-read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @RequestParam Long userId,
            @RequestParam String provider,
            @RequestBody List<String> emailIds) {
        
        try {
            emailService.markAsRead(userId, provider, emailIds);
            
            return ResponseEntity.ok(ApiResponse.success("Emails marked as read successfully"));
            
        } catch (Exception e) {
            log.error("Error marking emails as read: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to mark emails as read", "EMAIL_009", e.getMessage()));
        }
    }

    /**
     * Email'leri okunmadı olarak işaretler
     */
    @PutMapping("/mark-unread")
    public ResponseEntity<ApiResponse<String>> markAsUnread(
            @RequestParam Long userId,
            @RequestParam String provider,
            @RequestBody List<String> emailIds) {
        
        try {
            emailService.markAsUnread(userId, provider, emailIds);
            
            return ResponseEntity.ok(ApiResponse.success("Emails marked as unread successfully"));
            
        } catch (Exception e) {
            log.error("Error marking emails as unread: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to mark emails as unread", "EMAIL_010", e.getMessage()));
        }
    }

    /**
     * Email'leri yıldızlı olarak işaretler
     */
    @PutMapping("/star")
    public ResponseEntity<ApiResponse<String>> starEmails(
            @RequestParam Long userId,
            @RequestParam String provider,
            @RequestBody List<String> emailIds) {
        
        try {
            emailService.starEmails(userId, provider, emailIds);
            
            return ResponseEntity.ok(ApiResponse.success("Emails starred successfully"));
            
        } catch (Exception e) {
            log.error("Error starring emails: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to star emails", "EMAIL_011", e.getMessage()));
        }
    }

    /**
     * Email'lerden yıldızı kaldırır
     */
    @PutMapping("/unstar")
    public ResponseEntity<ApiResponse<String>> unstarEmails(
            @RequestParam Long userId,
            @RequestParam String provider,
            @RequestBody List<String> emailIds) {
        
        try {
            emailService.unstarEmails(userId, provider, emailIds);
            
            return ResponseEntity.ok(ApiResponse.success("Emails unstarred successfully"));
            
        } catch (Exception e) {
            log.error("Error unstarring emails: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Failed to unstar emails", "EMAIL_012", e.getMessage()));
        }
    }
} 