package com.prospect.crm.service;

import com.prospect.crm.constant.OAuthProvider;
import com.prospect.crm.dto.EmailSendRequestDto;
import com.prospect.crm.dto.EmailReadRequestDto;
import com.prospect.crm.dto.EmailReadResponseDto;
import com.prospect.crm.dto.EmailDeleteRequestDto;
import com.prospect.crm.dto.EmailListRequestDto;
import com.prospect.crm.dto.EmailListResponseDto;
import com.prospect.crm.model.OauthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailService {

    private final OAuthService oAuthService;
    private final SystemLogService systemLogService;
    private final DailyEmailLimitService dailyEmailLimitService;
    private final BounceEmailService bounceEmailService;
    private final EmailLogService emailLogService;
    private final RestTemplate restTemplate;

    public EmailService(OAuthService oAuthService, SystemLogService systemLogService, DailyEmailLimitService dailyEmailLimitService, BounceEmailService bounceEmailService, EmailLogService emailLogService) {
        this.oAuthService = oAuthService;
        this.systemLogService = systemLogService;
        this.dailyEmailLimitService = dailyEmailLimitService;
        this.bounceEmailService = bounceEmailService;
        this.emailLogService = emailLogService;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Email gönderir (Gmail veya Outlook)
     */
    public void sendEmail(Long userId, EmailSendRequestDto request) {
        try {
            // Check daily email limit before sending (without incrementing)
            dailyEmailLimitService.validateEmailLimit(userId);
            
            // Check for bounce emails before sending
            checkAndFilterBounceEmails(request);
            
            OAuthProvider provider = OAuthProvider.fromCode(request.getProvider());
            
            switch (provider) {
                case GOOGLE -> sendEmailViaGmail(userId, request);
                case MICROSOFT -> sendEmailViaOutlook(userId, request);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + request.getProvider());
            }
            
            // Increment successful email count
            dailyEmailLimitService.incrementSuccessfulEmailCount(userId);
            
            // Log successful email send for each recipient
            for (String recipient : request.getToEmails()) {
                emailLogService.logEmailSent(userId, recipient, "SENT", null);
            }
            
            systemLogService.logBusiness("Email sent successfully", 
                "Provider: " + request.getProvider() + ", To: " + request.getToEmails(), 
                userId.toString());
                
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            
            // Log failed email send for each recipient
            for (String recipient : request.getToEmails()) {
                emailLogService.logEmailSent(userId, recipient, "FAILED", e.getMessage());
            }
            
            systemLogService.logError("Email sending failed", 
                "Provider: " + request.getProvider() + ", To: " + request.getToEmails(), 
                e.getMessage(), "EmailService", "sendEmail");
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Gmail API ile email gönderir
     */
    private void sendEmailViaGmail(Long userId, EmailSendRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), request.getFromEmail());
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Gmail");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Gmail API endpoint
            String gmailApiUrl = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";
            
            // Email içeriğini oluştur
            String emailContent = createGmailMessage(request);
            
            // Base64 encode
            String encodedMessage = Base64.getUrlEncoder().withoutPadding().encodeToString(emailContent.getBytes());
            
            // Request body
            Map<String, Object> requestBody = Map.of("raw", encodedMessage);
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            headers.set("Content-Type", "application/json");
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                gmailApiUrl, 
                HttpMethod.POST, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent via Gmail API successfully");
            } else {
                throw new RuntimeException("Gmail API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error sending email via Gmail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email via Gmail", e);
        }
    }

    /**
     * Outlook/Microsoft Graph API ile email gönderir
     */
    private void sendEmailViaOutlook(Long userId, EmailSendRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), request.getFromEmail());
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Outlook");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Microsoft Graph API endpoint
            String graphApiUrl = "https://graph.microsoft.com/v1.0/me/sendMail";
            
            // Request body oluştur
            Map<String, Object> requestBody = createOutlookMessage(request);
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            headers.set("Content-Type", "application/json");
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                graphApiUrl, 
                HttpMethod.POST, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent via Microsoft Graph API successfully");
            } else {
                throw new RuntimeException("Microsoft Graph API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error sending email via Outlook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email via Outlook", e);
        }
    }

    /**
     * Gmail için email içeriği oluşturur
     */
    private String createGmailMessage(EmailSendRequestDto request) {
        StringBuilder message = new StringBuilder();
        
        // Headers
        message.append("From: ").append(request.getFromEmail()).append("\r\n");
        message.append("To: ").append(String.join(",", request.getToEmails())).append("\r\n");
        
        if (request.getCcEmails() != null && !request.getCcEmails().isEmpty()) {
            message.append("Cc: ").append(String.join(",", request.getCcEmails())).append("\r\n");
        }
        
        if (request.getBccEmails() != null && !request.getBccEmails().isEmpty()) {
            message.append("Bcc: ").append(String.join(",", request.getBccEmails())).append("\r\n");
        }
        
        message.append("Subject: ").append(request.getSubject()).append("\r\n");
        message.append("Content-Type: ").append(request.getContentType()).append("; charset=UTF-8\r\n");
        message.append("\r\n");
        message.append(request.getBody());
        
        return message.toString();
    }

    /**
     * Outlook için email içeriği oluşturur
     */
    private Map<String, Object> createOutlookMessage(EmailSendRequestDto request) {
        // Message object
        Map<String, Object> message = Map.of(
            "subject", request.getSubject(),
            "body", Map.of(
                "contentType", request.getContentType(),
                "content", request.getBody()
            ),
            "toRecipients", request.getToEmails().stream()
                .map(email -> Map.of("emailAddress", Map.of("address", email)))
                .toList()
        );
        
        // Add CC recipients if present
        if (request.getCcEmails() != null && !request.getCcEmails().isEmpty()) {
            message = Map.of(
                "subject", request.getSubject(),
                "body", Map.of(
                    "contentType", request.getContentType(),
                    "content", request.getBody()
                ),
                "toRecipients", request.getToEmails().stream()
                    .map(email -> Map.of("emailAddress", Map.of("address", email)))
                    .toList(),
                "ccRecipients", request.getCcEmails().stream()
                    .map(email -> Map.of("emailAddress", Map.of("address", email)))
                    .toList()
            );
        }
        
        // Add BCC recipients if present
        if (request.getBccEmails() != null && !request.getBccEmails().isEmpty()) {
            message = Map.of(
                "subject", request.getSubject(),
                "body", Map.of(
                    "contentType", request.getContentType(),
                    "content", request.getBody()
                ),
                "toRecipients", request.getToEmails().stream()
                    .map(email -> Map.of("emailAddress", Map.of("address", email)))
                    .toList(),
                "bccRecipients", request.getBccEmails().stream()
                    .map(email -> Map.of("emailAddress", Map.of("address", email)))
                    .toList()
            );
        }
        
        return Map.of("message", message);
    }

    /**
     * SMTP ile email gönderir (fallback)
     */
    public void sendEmailViaSMTP(String fromEmail, String password, List<String> toEmails, 
                                String subject, String body, String contentType) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            
            for (String toEmail : toEmails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            }
            
            message.setSubject(subject);
            message.setContent(body, contentType);
            
            Transport.send(message);
            
            log.info("Email sent via SMTP successfully");
            
            // Log successful SMTP email send for each recipient
            // Note: We don't have userId here, so we'll use a system log instead
            systemLogService.logInfo("SMTP email sent successfully", 
                "From: " + fromEmail + ", To: " + toEmails + ", Subject: " + subject,
                "EmailService", "sendEmailViaSMTP");
            
        } catch (Exception e) {
            log.error("Error sending email via SMTP: {}", e.getMessage(), e);
            
            // Log failed SMTP email send
            systemLogService.logError("SMTP email sending failed", 
                "From: " + fromEmail + ", To: " + toEmails + ", Error: " + e.getMessage(),
                e.getStackTrace().toString(), "EmailService", "sendEmailViaSMTP");
            
            throw new RuntimeException("Failed to send email via SMTP", e);
        }
    }

    /**
     * Email template'ini render eder
     */
    public String renderEmailTemplate(String templateName, Map<String, Object> variables) {
        try {
            // Basit template rendering
            String template = getEmailTemplate(templateName);
            
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                template = template.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
            }
            
            // Log successful template rendering
            systemLogService.logInfo("Email template rendered successfully", 
                "Template: " + templateName + ", Variables: " + variables.keySet(),
                "EmailService", "renderEmailTemplate");
            
            return template;
            
        } catch (Exception e) {
            log.error("Error rendering email template: {}", e.getMessage(), e);
            
            // Log failed template rendering
            systemLogService.logError("Email template rendering failed", 
                "Template: " + templateName + ", Error: " + e.getMessage(),
                e.getStackTrace().toString(), "EmailService", "renderEmailTemplate");
            
            throw new RuntimeException("Failed to render email template", e);
        }
    }

    /**
     * Email template'ini getirir
     */
    private String getEmailTemplate(String templateName) {
        return switch (templateName) {
            case "welcome" -> """
                <html>
                <body>
                    <h1>Hoş Geldiniz!</h1>
                    <p>Merhaba {{name}},</p>
                    <p>Prospect CRM'e hoş geldiniz. Hesabınız başarıyla oluşturuldu.</p>
                    <p>Kullanıcı adınız: {{username}}</p>
                    <p>İyi çalışmalar!</p>
                </body>
                </html>
                """;
            case "lead_followup" -> """
                <html>
                <body>
                    <h1>Lead Takip</h1>
                    <p>Merhaba {{leadName}},</p>
                    <p>{{companyName}} ile ilgili görüşmemizi takip etmek istiyorum.</p>
                    <p>Size nasıl yardımcı olabilirim?</p>
                    <p>Saygılarımla,<br>{{senderName}}</p>
                </body>
                </html>
                """;
            default -> """
                <html>
                <body>
                    <p>{{content}}</p>
                </body>
                </html>
                """;
        };
    }

    // ==================== EMAIL OKUMA İŞLEMLERİ ====================

    /**
     * Email okur (Gmail veya Outlook)
     */
    public EmailReadResponseDto readEmail(Long userId, EmailReadRequestDto request) {
        try {
            OAuthProvider provider = OAuthProvider.fromCode(request.getProvider());
            
            EmailReadResponseDto response = switch (provider) {
                case GOOGLE -> readEmailViaGmail(userId, request);
                case MICROSOFT -> readEmailViaOutlook(userId, request);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + request.getProvider());
            };
            
            // Log successful email read
            emailLogService.logEmailRead(userId, request.getEmailId(), "SUCCESS", null);
            
            systemLogService.logBusiness("Email read successfully", 
                "Provider: " + request.getProvider() + ", Email ID: " + request.getEmailId(), 
                userId.toString());
                
            return response;
                
        } catch (Exception e) {
            log.error("Error reading email: {}", e.getMessage(), e);
            
            // Log failed email read
            emailLogService.logEmailRead(userId, request.getEmailId(), "FAILED", e.getMessage());
            
            systemLogService.logError("Email reading failed", 
                "Provider: " + request.getProvider() + ", Email ID: " + request.getEmailId(), 
                e.getMessage(), "EmailService", "readEmail");
            throw new RuntimeException("Failed to read email", e);
        }
    }

    /**
     * Gmail API ile email okur
     */
    private EmailReadResponseDto readEmailViaGmail(Long userId, EmailReadRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), null);
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Gmail");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Gmail API endpoint
            String gmailApiUrl = "https://gmail.googleapis.com/gmail/v1/users/me/messages/" + request.getEmailId();
            
            // Query parameters
            String format = request.getFormat() != null ? request.getFormat() : "full";
            String queryParams = "?format=" + format;
            
            if (request.getIncludeAttachments() != null && request.getIncludeAttachments()) {
                queryParams += "&metadataHeaders=Content-Type&metadataHeaders=Content-Disposition";
            }
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                gmailApiUrl + queryParams, 
                HttpMethod.GET, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapGmailResponseToEmailReadResponse(response.getBody(), request.getProvider());
            } else {
                throw new RuntimeException("Gmail API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error reading email via Gmail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read email via Gmail", e);
        }
    }

    /**
     * Outlook/Microsoft Graph API ile email okur
     */
    private EmailReadResponseDto readEmailViaOutlook(Long userId, EmailReadRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), null);
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Outlook");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Microsoft Graph API endpoint
            String graphApiUrl = "https://graph.microsoft.com/v1.0/me/messages/" + request.getEmailId();
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                graphApiUrl, 
                HttpMethod.GET, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapOutlookResponseToEmailReadResponse(response.getBody(), request.getProvider());
            } else {
                throw new RuntimeException("Microsoft Graph API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error reading email via Outlook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read email via Outlook", e);
        }
    }

    // ==================== EMAIL LİSTESİ İŞLEMLERİ ====================

    /**
     * Email listesini getirir (Gmail veya Outlook)
     */
    public EmailListResponseDto listEmails(Long userId, EmailListRequestDto request) {
        try {
            OAuthProvider provider = OAuthProvider.fromCode(request.getProvider());
            
            EmailListResponseDto response = switch (provider) {
                case GOOGLE -> listEmailsViaGmail(userId, request);
                case MICROSOFT -> listEmailsViaOutlook(userId, request);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + request.getProvider());
            };
            
            // Log successful email listing
            emailLogService.logEmailList(userId, request.getProvider(), "SUCCESS", null);
            
            systemLogService.logBusiness("Email list retrieved successfully", 
                "Provider: " + request.getProvider() + ", Label: " + request.getLabel() + ", Count: " + response.getEmails().size(), 
                userId.toString());
                
            return response;
                
        } catch (Exception e) {
            log.error("Error listing emails: {}", e.getMessage(), e);
            
            // Log failed email listing
            emailLogService.logEmailList(userId, request.getProvider(), "FAILED", e.getMessage());
            
            systemLogService.logError("Email listing failed", 
                "Provider: " + request.getProvider() + ", Label: " + request.getLabel(), 
                e.getMessage(), "EmailService", "listEmails");
            throw new RuntimeException("Failed to list emails", e);
        }
    }

    /**
     * Gmail API ile email listesi getirir
     */
    private EmailListResponseDto listEmailsViaGmail(Long userId, EmailListRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), null);
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Gmail");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Gmail API endpoint
            String gmailApiUrl = "https://gmail.googleapis.com/gmail/v1/users/me/messages";
            
            // Query parameters
            StringBuilder queryParams = new StringBuilder("?");
            
            if (request.getLabel() != null && !request.getLabel().isEmpty()) {
                queryParams.append("labelIds=").append(request.getLabel()).append("&");
            }
            
            if (request.getQuery() != null && !request.getQuery().isEmpty()) {
                queryParams.append("q=").append(request.getQuery()).append("&");
            }
            
            if (request.getMaxResults() != null) {
                queryParams.append("maxResults=").append(request.getMaxResults()).append("&");
            }
            
            if (request.getPageToken() != null && !request.getPageToken().isEmpty()) {
                queryParams.append("pageToken=").append(request.getPageToken()).append("&");
            }
            
            if (request.getIncludeSpamTrash() != null && request.getIncludeSpamTrash()) {
                queryParams.append("includeSpamTrash=true&");
            }
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                gmailApiUrl + queryParams.toString(), 
                HttpMethod.GET, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapGmailListResponseToEmailListResponse(response.getBody(), request.getProvider(), request.getLabel());
            } else {
                throw new RuntimeException("Gmail API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error listing emails via Gmail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list emails via Gmail", e);
        }
    }

    /**
     * Outlook/Microsoft Graph API ile email listesi getirir
     */
    private EmailListResponseDto listEmailsViaOutlook(Long userId, EmailListRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), null);
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Outlook");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Microsoft Graph API endpoint
            String graphApiUrl = "https://graph.microsoft.com/v1.0/me/messages";
            
            // Query parameters
            StringBuilder queryParams = new StringBuilder("?");
            
            if (request.getMaxResults() != null) {
                queryParams.append("$top=").append(request.getMaxResults()).append("&");
            }
            
            if (request.getOrderBy() != null && !request.getOrderBy().isEmpty()) {
                queryParams.append("$orderby=").append(request.getOrderBy());
                if (request.getSortOrder() != null && !request.getSortOrder().isEmpty()) {
                    queryParams.append(" ").append(request.getSortOrder());
                }
                queryParams.append("&");
            }
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token.getAccessToken());
            
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                graphApiUrl + queryParams.toString(), 
                HttpMethod.GET, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapOutlookListResponseToEmailListResponse(response.getBody(), request.getProvider(), request.getLabel());
            } else {
                throw new RuntimeException("Microsoft Graph API error: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error listing emails via Outlook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list emails via Outlook", e);
        }
    }

    // ==================== EMAIL SİLME İŞLEMLERİ ====================

    /**
     * Email'leri siler (Gmail veya Outlook)
     */
    public void deleteEmails(Long userId, EmailDeleteRequestDto request) {
        try {
            OAuthProvider provider = OAuthProvider.fromCode(request.getProvider());
            
            switch (provider) {
                case GOOGLE -> deleteEmailsViaGmail(userId, request);
                case MICROSOFT -> deleteEmailsViaOutlook(userId, request);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + request.getProvider());
            }
            
            // Log successful email deletion for each email ID
            for (String emailId : request.getEmailIds()) {
                emailLogService.logEmailDelete(userId, emailId, "SUCCESS", null);
            }
            
            systemLogService.logBusiness("Emails deleted successfully", 
                "Provider: " + request.getProvider() + ", Email IDs: " + request.getEmailIds() + ", Permanent: " + request.getPermanentDelete(), 
                userId.toString());
                
        } catch (Exception e) {
            log.error("Error deleting emails: {}", e.getMessage(), e);
            
            // Log failed email deletion for each email ID
            for (String emailId : request.getEmailIds()) {
                emailLogService.logEmailDelete(userId, emailId, "FAILED", e.getMessage());
            }
            
            systemLogService.logError("Email deletion failed", 
                "Provider: " + request.getProvider() + ", Email IDs: " + request.getEmailIds(), 
                e.getMessage(), "EmailService", "deleteEmails");
            throw new RuntimeException("Failed to delete emails", e);
        }
    }

    /**
     * Gmail API ile email'leri siler
     */
    private void deleteEmailsViaGmail(Long userId, EmailDeleteRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), null);
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Gmail");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Her email için silme işlemi
            for (String emailId : request.getEmailIds()) {
                String gmailApiUrl = "https://gmail.googleapis.com/gmail/v1/users/me/messages/" + emailId;
                
                // Headers
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token.getAccessToken());
                
                HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    gmailApiUrl, 
                    HttpMethod.DELETE, 
                    requestEntity, 
                    new ParameterizedTypeReference<Map<String, Object>>() {}
                );
                
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Gmail API error: " + response.getStatusCode());
                }
            }
            
        } catch (Exception e) {
            log.error("Error deleting emails via Gmail: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete emails via Gmail", e);
        }
    }

    /**
     * Outlook/Microsoft Graph API ile email'leri siler
     */
    private void deleteEmailsViaOutlook(Long userId, EmailDeleteRequestDto request) {
        try {
            Optional<OauthToken> tokenOpt = oAuthService.getValidToken(userId, request.getProvider(), null);
            
            if (tokenOpt.isEmpty()) {
                throw new RuntimeException("No valid OAuth token found for Outlook");
            }
            
            OauthToken token = tokenOpt.get();
            
            // Her email için silme işlemi
            for (String emailId : request.getEmailIds()) {
                String graphApiUrl = "https://graph.microsoft.com/v1.0/me/messages/" + emailId;
                
                // Headers
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token.getAccessToken());
                
                HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    graphApiUrl, 
                    HttpMethod.DELETE, 
                    requestEntity, 
                    new ParameterizedTypeReference<Map<String, Object>>() {}
                );
                
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Microsoft Graph API error: " + response.getStatusCode());
                }
            }
            
        } catch (Exception e) {
            log.error("Error deleting emails via Outlook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete emails via Outlook", e);
        }
    }

    // ==================== EMAIL ETİKETLEME İŞLEMLERİ ====================

    /**
     * Email'leri okundu olarak işaretler
     */
    public void markAsRead(Long userId, String provider, List<String> emailIds) {
        try {
            OAuthProvider oAuthProvider = OAuthProvider.fromCode(provider);
            
            switch (oAuthProvider) {
                case GOOGLE -> markAsReadViaGmail(userId, emailIds);
                case MICROSOFT -> markAsReadViaOutlook(userId, emailIds);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + provider);
            }
            
            // Log successful mark as read for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "MARK_READ", "SUCCESS", null);
            }
            
            systemLogService.logBusiness("Emails marked as read", 
                "Provider: " + provider + ", Email IDs: " + emailIds, 
                userId.toString());
                
        } catch (Exception e) {
            log.error("Error marking emails as read: {}", e.getMessage(), e);
            
            // Log failed mark as read for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "MARK_READ", "FAILED", e.getMessage());
            }
            
            throw new RuntimeException("Failed to mark emails as read", e);
        }
    }

    /**
     * Email'leri okunmadı olarak işaretler
     */
    public void markAsUnread(Long userId, String provider, List<String> emailIds) {
        try {
            OAuthProvider oAuthProvider = OAuthProvider.fromCode(provider);
            
            switch (oAuthProvider) {
                case GOOGLE -> markAsUnreadViaGmail(userId, emailIds);
                case MICROSOFT -> markAsUnreadViaOutlook(userId, emailIds);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + provider);
            }
            
            // Log successful mark as unread for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "MARK_UNREAD", "SUCCESS", null);
            }
            
            systemLogService.logBusiness("Emails marked as unread", 
                "Provider: " + provider + ", Email IDs: " + emailIds, 
                userId.toString());
                
        } catch (Exception e) {
            log.error("Error marking emails as unread: {}", e.getMessage(), e);
            
            // Log failed mark as unread for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "MARK_UNREAD", "FAILED", e.getMessage());
            }
            
            throw new RuntimeException("Failed to mark emails as unread", e);
        }
    }

    /**
     * Email'leri yıldızlı olarak işaretler
     */
    public void starEmails(Long userId, String provider, List<String> emailIds) {
        try {
            OAuthProvider oAuthProvider = OAuthProvider.fromCode(provider);
            
            switch (oAuthProvider) {
                case GOOGLE -> starEmailsViaGmail(userId, emailIds);
                case MICROSOFT -> starEmailsViaOutlook(userId, emailIds);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + provider);
            }
            
            // Log successful star for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "STAR", "SUCCESS", null);
            }
            
            systemLogService.logBusiness("Emails starred", 
                "Provider: " + provider + ", Email IDs: " + emailIds, 
                userId.toString());
                
        } catch (Exception e) {
            log.error("Error starring emails: {}", e.getMessage(), e);
            
            // Log failed star for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "STAR", "FAILED", e.getMessage());
            }
            
            throw new RuntimeException("Failed to star emails", e);
        }
    }

    /**
     * Email'lerden yıldızı kaldırır
     */
    public void unstarEmails(Long userId, String provider, List<String> emailIds) {
        try {
            OAuthProvider oAuthProvider = OAuthProvider.fromCode(provider);
            
            switch (oAuthProvider) {
                case GOOGLE -> unstarEmailsViaGmail(userId, emailIds);
                case MICROSOFT -> unstarEmailsViaOutlook(userId, emailIds);
                default -> throw new IllegalArgumentException("Unsupported email provider: " + provider);
            }
            
            // Log successful unstar for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "UNSTAR", "SUCCESS", null);
            }
            
            systemLogService.logBusiness("Emails unstarred", 
                "Provider: " + provider + ", Email IDs: " + emailIds, 
                userId.toString());
                
        } catch (Exception e) {
            log.error("Error unstarring emails: {}", e.getMessage(), e);
            
            // Log failed unstar for each email ID
            for (String emailId : emailIds) {
                emailLogService.logEmailAction(userId, emailId, "UNSTAR", "FAILED", e.getMessage());
            }
            
            throw new RuntimeException("Failed to unstar emails", e);
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Check and filter bounce emails from the request
     */
    private void checkAndFilterBounceEmails(EmailSendRequestDto request) {
        try {
            // Filter out emails with recent hard bounces
            if (request.getToEmails() != null) {
                List<String> filteredToEmails = request.getToEmails().stream()
                        .filter(email -> !bounceEmailService.hasRecentHardBounces(email))
                        .collect(Collectors.toList());
                
                if (filteredToEmails.size() != request.getToEmails().size()) {
                    List<String> bouncedEmails = request.getToEmails().stream()
                            .filter(email -> bounceEmailService.hasRecentHardBounces(email))
                            .collect(Collectors.toList());
                    
                    systemLogService.logWarn("Bounce emails filtered out", 
                        "Filtered emails: " + bouncedEmails + ", Original count: " + request.getToEmails().size() + ", Filtered count: " + filteredToEmails.size(),
                        "EmailService", "checkAndFilterBounceEmails");
                    
                    // Update request with filtered emails
                    request.setToEmails(filteredToEmails);
                }
            }
            
            // Filter CC emails
            if (request.getCcEmails() != null) {
                List<String> filteredCcEmails = request.getCcEmails().stream()
                        .filter(email -> !bounceEmailService.hasRecentHardBounces(email))
                        .collect(Collectors.toList());
                request.setCcEmails(filteredCcEmails);
            }
            
            // Filter BCC emails
            if (request.getBccEmails() != null) {
                List<String> filteredBccEmails = request.getBccEmails().stream()
                        .filter(email -> !bounceEmailService.hasRecentHardBounces(email))
                        .collect(Collectors.toList());
                request.setBccEmails(filteredBccEmails);
            }
            
        } catch (Exception e) {
            log.error("Error checking bounce emails: {}", e.getMessage(), e);
            systemLogService.logError("Failed to check bounce emails", 
                e.getMessage(), e.getStackTrace().toString(), "EmailService", "checkAndFilterBounceEmails");
        }
    }

    private EmailReadResponseDto mapGmailResponseToEmailReadResponse(Map<String, Object> gmailResponse, String provider) {
        // Gmail API response'unu EmailReadResponseDto'ya çevir
        // Bu metod Gmail API response yapısına göre implement edilecek
        return EmailReadResponseDto.builder()
                .provider(provider)
                .id((String) gmailResponse.get("id"))
                .threadId((String) gmailResponse.get("threadId"))
                .build();
    }

    private EmailReadResponseDto mapOutlookResponseToEmailReadResponse(Map<String, Object> outlookResponse, String provider) {
        // Microsoft Graph API response'unu EmailReadResponseDto'ya çevir
        // Bu metod Microsoft Graph API response yapısına göre implement edilecek
        return EmailReadResponseDto.builder()
                .provider(provider)
                .id((String) outlookResponse.get("id"))
                .subject((String) outlookResponse.get("subject"))
                .build();
    }

    private EmailListResponseDto mapGmailListResponseToEmailListResponse(Map<String, Object> gmailResponse, String provider, String label) {
        // Gmail API list response'unu EmailListResponseDto'ya çevir
        return EmailListResponseDto.builder()
                .provider(provider)
                .label(label)
                .nextPageToken((String) gmailResponse.get("nextPageToken"))
                .resultSizeEstimate((Integer) gmailResponse.get("resultSizeEstimate"))
                .build();
    }

    private EmailListResponseDto mapOutlookListResponseToEmailListResponse(Map<String, Object> outlookResponse, String provider, String label) {
        // Microsoft Graph API list response'unu EmailListResponseDto'ya çevir
        return EmailListResponseDto.builder()
                .provider(provider)
                .label(label)
                .build();
    }

    // Gmail etiketleme metodları
    private void markAsReadViaGmail(Long userId, List<String> emailIds) {
        // Gmail API ile okundu işaretleme
    }

    private void markAsUnreadViaGmail(Long userId, List<String> emailIds) {
        // Gmail API ile okunmadı işaretleme
    }

    private void starEmailsViaGmail(Long userId, List<String> emailIds) {
        // Gmail API ile yıldızlama
    }

    private void unstarEmailsViaGmail(Long userId, List<String> emailIds) {
        // Gmail API ile yıldız kaldırma
    }

    // Outlook etiketleme metodları
    private void markAsReadViaOutlook(Long userId, List<String> emailIds) {
        // Microsoft Graph API ile okundu işaretleme
    }

    private void markAsUnreadViaOutlook(Long userId, List<String> emailIds) {
        // Microsoft Graph API ile okunmadı işaretleme
    }

    private void starEmailsViaOutlook(Long userId, List<String> emailIds) {
        // Microsoft Graph API ile yıldızlama
    }

    private void unstarEmailsViaOutlook(Long userId, List<String> emailIds) {
        // Microsoft Graph API ile yıldız kaldırma
    }
} 