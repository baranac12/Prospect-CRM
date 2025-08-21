package com.prospect.crm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.*;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.EmailDraft;
import com.prospect.crm.model.Lead;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.EmailDraftRepository;
import com.prospect.crm.repository.LeadRepository;
import com.prospect.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailDraftService {

    private final EmailDraftRepository emailDraftRepository;
    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final EmailService emailService;
    private final SystemLogService systemLogService;
    private final DailyEmailLimitService dailyEmailLimitService;
    private final EmailLogService emailLogService;
    private final ObjectMapper objectMapper;

    // ==================== DRAFT CRUD İŞLEMLERİ ====================

    @Transactional
    public EmailDraftResponseDto createDraft(Long userId, EmailDraftRequestDto request) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage() + " : " + userId));

            EmailDraft draft = new EmailDraft();
            draft.setUserId(user);
            draft.setSubject(request.getSubject());
            draft.setBody(request.getBody());
            draft.setContentType(request.getContentType() != null ? request.getContentType() : "text/plain");
            draft.setCreatedByRobot(false);
            draft.setStatus("DRAFT");
            draft.setCreatedAt(LocalDateTime.now());
            draft.setUpdatedAt(LocalDateTime.now());
            draft.setProvider(request.getProvider() != null ? request.getProvider() : "SMTP");
            draft.setTemplateName(request.getTemplateName());
            draft.setTemplateData(request.getTemplateData());

            // Set lead if provided
            if (request.getLeadId() != null) {
                Lead lead = leadRepository.findById(request.getLeadId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LEAD_NOT_FOUND.getMessage() + " : " + request.getLeadId()));
                draft.setLeadId(lead);
            }

            // Convert lists to comma-separated strings
            draft.setToEmails(convertListToString(request.getToEmails()));
            draft.setCcEmails(convertListToString(request.getCcEmails()));
            draft.setBccEmails(convertListToString(request.getBccEmails()));
            draft.setAttachments(convertAttachmentsToString(request.getAttachments()));

            EmailDraft savedDraft = emailDraftRepository.save(draft);
            
            systemLogService.logInfo("Email draft created", 
                "Draft created for user: " + userId + ", Subject: " + request.getSubject(),
                "EmailDraftService", "createDraft");

            return mapToResponseDto(savedDraft);
        } catch (Exception e) {
            systemLogService.logError("Failed to create email draft", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "createDraft");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_CREATION_FAILED.getMessage());
        }
    }

    /**
     * Robot tarafından email draft oluşturur
     */
    @Transactional
    public EmailDraftResponseDto createRobotDraft(Long userId, EmailDraftRequestDto request) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage() + " : " + userId));

            EmailDraft draft = new EmailDraft();
            draft.setUserId(user);
            draft.setSubject(request.getSubject());
            draft.setBody(request.getBody());
            draft.setContentType(request.getContentType() != null ? request.getContentType() : "text/html");
            draft.setCreatedByRobot(true); // Robot tarafından oluşturuldu
            draft.setStatus("DRAFT");
            draft.setCreatedAt(LocalDateTime.now());
            draft.setUpdatedAt(LocalDateTime.now());
            draft.setProvider(request.getProvider() != null ? request.getProvider() : "SMTP");
            draft.setTemplateName(request.getTemplateName());
            draft.setTemplateData(request.getTemplateData());

            // Set lead if provided
            if (request.getLeadId() != null) {
                Lead lead = leadRepository.findById(request.getLeadId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LEAD_NOT_FOUND.getMessage() + " : " + request.getLeadId()));
                draft.setLeadId(lead);
            }

            // Convert lists to comma-separated strings
            draft.setToEmails(convertListToString(request.getToEmails()));
            draft.setCcEmails(convertListToString(request.getCcEmails()));
            draft.setBccEmails(convertListToString(request.getBccEmails()));
            draft.setAttachments(convertAttachmentsToString(request.getAttachments()));

            EmailDraft savedDraft = emailDraftRepository.save(draft);
            
            systemLogService.logInfo("Robot email draft created", 
                "Robot draft created for user: " + userId + ", Subject: " + request.getSubject(),
                "EmailDraftService", "createRobotDraft");

            return mapToResponseDto(savedDraft);
        } catch (Exception e) {
            systemLogService.logError("Failed to create robot email draft", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "createRobotDraft");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_CREATION_FAILED.getMessage());
        }
    }

    /**
     * Kullanıcının robot tarafından oluşturulan draftlarını listeler
     */
    @Transactional(readOnly = true)
    public Page<EmailDraftResponseDto> getRobotDrafts(Long userId, Pageable pageable) {
        try {
            Page<EmailDraft> drafts = emailDraftRepository.findByUserIdIdAndCreatedByRobotTrueOrderByCreatedAtDesc(userId, pageable);
            return drafts.map(this::mapToResponseDto);
        } catch (Exception e) {
            systemLogService.logError("Failed to get robot drafts", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "getRobotDrafts");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_RETRIEVAL_FAILED.getMessage());
        }
    }

    /**
     * Kullanıcının tüm draftlarını listeler (robot ve manuel)
     */
    @Transactional(readOnly = true)
    public Page<EmailDraftResponseDto> getAllDrafts(Long userId, Pageable pageable) {
        try {
            Page<EmailDraft> drafts = emailDraftRepository.findByUserIdIdOrderByCreatedAtDesc(userId, pageable);
            return drafts.map(this::mapToResponseDto);
        } catch (Exception e) {
            systemLogService.logError("Failed to get all drafts", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "getAllDrafts");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_RETRIEVAL_FAILED.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public EmailDraftResponseDto getDraft(Long userId, Long draftId) {
        try {
            EmailDraft draft = emailDraftRepository.findById(draftId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EMAIL_DRAFT_NOT_FOUND.getMessage() + " : " + draftId));

            // Check if user owns this draft
            if (!draft.getUserId().getId().equals(userId)) {
                throw new ValidationException("Access denied: User does not own this draft");
            }

            return mapToResponseDto(draft);
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            systemLogService.logError("Failed to get email draft", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "getDraft");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_RETRIEVAL_FAILED.getMessage());
        }
    }

    @Transactional
    public EmailDraftResponseDto updateDraft(Long userId, Long draftId, EmailDraftRequestDto request) {
        try {
            EmailDraft draft = emailDraftRepository.findById(draftId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EMAIL_DRAFT_NOT_FOUND.getMessage() + " : " + draftId));

            // Check if user owns this draft
            if (!draft.getUserId().getId().equals(userId)) {
                throw new ValidationException("Access denied: User does not own this draft");
            }

            // Check if draft can be updated (not sent)
            if ("SENT".equals(draft.getStatus())) {
                throw new ValidationException(ErrorCode.EMAIL_DRAFT_ALREADY_SENT.getMessage());
            }

            // Update fields
            draft.setSubject(request.getSubject());
            draft.setBody(request.getBody());
            draft.setContentType(request.getContentType() != null ? request.getContentType() : "text/plain");
            draft.setUpdatedAt(LocalDateTime.now());
            draft.setProvider(request.getProvider() != null ? request.getProvider() : draft.getProvider());
            draft.setTemplateName(request.getTemplateName());
            draft.setTemplateData(request.getTemplateData());

            // Update lead if provided
            if (request.getLeadId() != null) {
                Lead lead = leadRepository.findById(request.getLeadId())
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LEAD_NOT_FOUND.getMessage() + " : " + request.getLeadId()));
                draft.setLeadId(lead);
            }

            // Update email lists
            draft.setToEmails(convertListToString(request.getToEmails()));
            draft.setCcEmails(convertListToString(request.getCcEmails()));
            draft.setBccEmails(convertListToString(request.getBccEmails()));
            draft.setAttachments(convertAttachmentsToString(request.getAttachments()));

            EmailDraft updatedDraft = emailDraftRepository.save(draft);
            
            systemLogService.logInfo("Email draft updated", 
                "Draft updated for user: " + userId + ", Draft ID: " + draftId,
                "EmailDraftService", "updateDraft");

            return mapToResponseDto(updatedDraft);
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            systemLogService.logError("Failed to update email draft", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "updateDraft");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_UPDATE_FAILED.getMessage());
        }
    }

    @Transactional
    public void deleteDraft(Long userId, Long draftId) {
        try {
            EmailDraft draft = emailDraftRepository.findById(draftId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EMAIL_DRAFT_NOT_FOUND.getMessage() + " : " + draftId));

            // Check if user owns this draft
            if (!draft.getUserId().getId().equals(userId)) {
                throw new ValidationException("Access denied: User does not own this draft");
            }

            emailDraftRepository.delete(draft);
            
            systemLogService.logInfo("Email draft deleted", 
                "Draft deleted for user: " + userId + ", Draft ID: " + draftId,
                "EmailDraftService", "deleteDraft");
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            systemLogService.logError("Failed to delete email draft", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "deleteDraft");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_DELETION_FAILED.getMessage());
        }
    }

    // ==================== DRAFT LİSTESİ İŞLEMLERİ ====================

    @Transactional(readOnly = true)
    public EmailDraftListResponseDto listDrafts(Long userId, EmailDraftListRequestDto request) {
        try {
            // Build pageable
            Pageable pageable = buildPageable(request);
            
            // Get drafts based on filters
            Page<EmailDraft> draftPage;
            
            if (request.getStatus() != null && !"ALL".equals(request.getStatus())) {
                draftPage = emailDraftRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(
                    userRepository.getReferenceById(userId), request.getStatus(), pageable);
            } else {
                draftPage = emailDraftRepository.findByUserIdOrderByUpdatedAtDesc(
                    userRepository.getReferenceById(userId), pageable);
            }

            // Apply additional filters if needed
            List<EmailDraft> filteredDrafts = draftPage.getContent().stream()
                    .filter(draft -> filterDraft(draft, request))
                    .collect(Collectors.toList());

            // Convert to response DTOs
            List<EmailDraftListItemDto> draftItems = filteredDrafts.stream()
                    .map(this::mapToListItemDto)
                    .collect(Collectors.toList());

            return EmailDraftListResponseDto.builder()
                    .drafts(draftItems)
                    .totalElements((int) draftPage.getTotalElements())
                    .totalPages(draftPage.getTotalPages())
                    .currentPage(draftPage.getNumber())
                    .pageSize(draftPage.getSize())
                    .hasNext(draftPage.hasNext())
                    .hasPrevious(draftPage.hasPrevious())
                    .build();

        } catch (Exception e) {
            systemLogService.logError("Failed to list email drafts", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "listDrafts");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_LIST_FAILED.getMessage());
        }
    }

    // ==================== DRAFT GÖNDERME İŞLEMLERİ ====================

    @Transactional
    public void sendDraft(Long userId, Long draftId) {
        try {
            EmailDraft draft = emailDraftRepository.findById(draftId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EMAIL_DRAFT_NOT_FOUND.getMessage() + " : " + draftId));

            // Check if user owns this draft
            if (!draft.getUserId().getId().equals(userId)) {
                throw new ValidationException("Access denied: User does not own this draft");
            }

            // Check if draft can be sent
            if ("SENT".equals(draft.getStatus())) {
                throw new ValidationException(ErrorCode.EMAIL_DRAFT_ALREADY_SENT.getMessage());
            }

            // Check daily email limit before sending (without incrementing)
            dailyEmailLimitService.validateEmailLimit(userId);

            // Get user's OAuth provider preference or default to SMTP
            String provider = draft.getProvider() != null ? draft.getProvider() : "SMTP";
            
            // Get user's email for OAuth token lookup
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage() + " : " + userId));
            
            String userEmail = user.getEmail();

            // Prepare email request with OAuth provider
            EmailSendRequestDto emailRequest = EmailSendRequestDto.builder()
                    .toEmails(convertStringToList(draft.getToEmails()))
                    .ccEmails(convertStringToList(draft.getCcEmails()))
                    .bccEmails(convertStringToList(draft.getBccEmails()))
                    .subject(draft.getSubject())
                    .body(draft.getBody())
                    .contentType(draft.getContentType())
                    .attachments(convertStringToAttachments(draft.getAttachments()))
                    .provider(provider)
                    .fromEmail(userEmail) // Use user's email as from address
                    .build();

            // Send email using OAuth or SMTP
            emailService.sendEmail(userId, emailRequest);

            // Update draft status
            draft.setStatus("SENT");
            draft.setSentAt(LocalDateTime.now());
            draft.setUpdatedAt(LocalDateTime.now());
            emailDraftRepository.save(draft);

            // Log successful draft sending for each recipient
            List<String> allRecipients = convertStringToList(draft.getToEmails());
            for (String recipient : allRecipients) {
                emailLogService.logEmailSent(userId, recipient, "DRAFT_SENT", null, draft);
            }

            systemLogService.logInfo("Email draft sent", 
                "Draft sent for user: " + userId + ", Draft ID: " + draftId + ", Provider: " + provider,
                "EmailDraftService", "sendDraft");

        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            // Log failed draft sending for each recipient
            try {
                EmailDraft draft = emailDraftRepository.findById(draftId).orElse(null);
                if (draft != null) {
                    List<String> allRecipients = convertStringToList(draft.getToEmails());
                    for (String recipient : allRecipients) {
                        emailLogService.logEmailSent(userId, recipient, "DRAFT_FAILED", e.getMessage(), draft);
                    }
                }
            } catch (Exception logException) {
                log.error("Failed to log draft sending failure", logException);
            }
            
            systemLogService.logError("Failed to send email draft", e.getMessage(), e.getStackTrace().toString(), 
                "EmailDraftService", "sendDraft");
            throw new RuntimeException(ErrorCode.EMAIL_DRAFT_SEND_FAILED.getMessage());
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private String convertListToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }

    private List<String> convertStringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return List.of();
        }
        return List.of(str.split(","));
    }

    private String convertAttachmentsToString(List<EmailAttachmentDto> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attachments);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize attachments", e);
            return null;
        }
    }

    private List<EmailAttachmentDto> convertStringToAttachments(String attachmentsStr) {
        if (attachmentsStr == null || attachmentsStr.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(attachmentsStr, new TypeReference<List<EmailAttachmentDto>>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize attachments", e);
            return List.of();
        }
    }

    private Pageable buildPageable(EmailDraftListRequestDto request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "updatedAt";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortOrder()) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private boolean filterDraft(EmailDraft draft, EmailDraftListRequestDto request) {
        // Filter by lead
        if (request.getLeadId() != null && 
            (draft.getLeadId() == null || !draft.getLeadId().getId().equals(request.getLeadId()))) {
            return false;
        }

        // Filter by provider
        if (request.getProvider() != null && !request.getProvider().equals(draft.getProvider())) {
            return false;
        }

        // Filter by template
        if (request.getTemplateName() != null && !request.getTemplateName().equals(draft.getTemplateName())) {
            return false;
        }

        // Filter by creation type
        if (request.getCreatedByRobot() != null && !request.getCreatedByRobot().equals(draft.getCreatedByRobot())) {
            return false;
        }

        // Filter by search term
        if (request.getSearchTerm() != null && !request.getSearchTerm().trim().isEmpty()) {
            String searchTerm = request.getSearchTerm().toLowerCase();
            String subject = draft.getSubject() != null ? draft.getSubject().toLowerCase() : "";
            String body = draft.getBody() != null ? draft.getBody().toLowerCase() : "";
            String toEmails = draft.getToEmails() != null ? draft.getToEmails().toLowerCase() : "";
            
            if (!subject.contains(searchTerm) && !body.contains(searchTerm) && !toEmails.contains(searchTerm)) {
                return false;
            }
        }

        return true;
    }

    private EmailDraftResponseDto mapToResponseDto(EmailDraft draft) {
        return EmailDraftResponseDto.builder()
                .id(draft.getId())
                .userId(draft.getUserId() != null ? draft.getUserId().getId() : null)
                .leadId(draft.getLeadId() != null ? draft.getLeadId().getId() : null)
                .subject(draft.getSubject())
                .body(draft.getBody())
                .contentType(draft.getContentType())
                .toEmails(convertStringToList(draft.getToEmails()))
                .ccEmails(convertStringToList(draft.getCcEmails()))
                .bccEmails(convertStringToList(draft.getBccEmails()))
                .attachments(convertStringToAttachments(draft.getAttachments()))
                .createdByRobot(draft.getCreatedByRobot())
                .status(draft.getStatus())
                .createdAt(draft.getCreatedAt())
                .updatedAt(draft.getUpdatedAt())
                .sentAt(draft.getSentAt())
                .provider(draft.getProvider())
                .templateName(draft.getTemplateName())
                .templateData(draft.getTemplateData())
                .build();
    }

    private EmailDraftListItemDto mapToListItemDto(EmailDraft draft) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return EmailDraftListItemDto.builder()
                .id(draft.getId())
                .userId(draft.getUserId() != null ? draft.getUserId().getId() : null)
                .leadId(draft.getLeadId() != null ? draft.getLeadId().getId() : null)
                .subject(draft.getSubject())
                .body(draft.getBody())
                .contentType(draft.getContentType())
                .toEmails(convertStringToList(draft.getToEmails()))
                .ccEmails(convertStringToList(draft.getCcEmails()))
                .bccEmails(convertStringToList(draft.getBccEmails()))
                .createdByRobot(draft.getCreatedByRobot())
                .status(draft.getStatus())
                .provider(draft.getProvider())
                .templateName(draft.getTemplateName())
                .createdAt(draft.getCreatedAt() != null ? draft.getCreatedAt().format(formatter) : null)
                .updatedAt(draft.getUpdatedAt() != null ? draft.getUpdatedAt().format(formatter) : null)
                .sentAt(draft.getSentAt() != null ? draft.getSentAt().format(formatter) : null)
                .hasAttachments(draft.getAttachments() != null && !draft.getAttachments().trim().isEmpty())
                .build();
    }
} 