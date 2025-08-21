package com.prospect.crm.controller;

import com.prospect.crm.dto.*;
import com.prospect.crm.service.EmailDraftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/email-drafts")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class EmailDraftController {

    private final EmailDraftService emailDraftService;

    // ==================== DRAFT CRUD İŞLEMLERİ ====================

    @PostMapping
    public ResponseEntity<ApiResponse<EmailDraftResponseDto>> createDraft(
            @RequestBody EmailDraftRequestDto request, @RequestParam Long userId) {
        try {
            EmailDraftResponseDto draft = emailDraftService.createDraft(userId, request);
            return ResponseEntity.ok(ApiResponse.success(draft, "Email draft created successfully"));
        } catch (Exception e) {
            log.error("Failed to create email draft", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create email draft: " + e.getMessage(), "ERR_7007"));
        }
    }

    @GetMapping("/{draftId}")
    public ResponseEntity<ApiResponse<EmailDraftResponseDto>> getDraft(
            @PathVariable Long draftId, @RequestParam Long userId) {
        try {
            EmailDraftResponseDto draft = emailDraftService.getDraft(userId, draftId);
            return ResponseEntity.ok(ApiResponse.success(draft, "Email draft retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get email draft", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get email draft: " + e.getMessage(), "ERR_7010"));
        }
    }

    @PutMapping("/{draftId}")
    public ResponseEntity<ApiResponse<EmailDraftResponseDto>> updateDraft(
            @PathVariable Long draftId, @RequestBody EmailDraftRequestDto request, @RequestParam Long userId) {
        try {
            EmailDraftResponseDto draft = emailDraftService.updateDraft(userId, draftId, request);
            return ResponseEntity.ok(ApiResponse.success(draft, "Email draft updated successfully"));
        } catch (Exception e) {
            log.error("Failed to update email draft", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update email draft: " + e.getMessage(), "ERR_7008"));
        }
    }

    @DeleteMapping("/{draftId}")
    public ResponseEntity<ApiResponse<String>> deleteDraft(
            @PathVariable Long draftId, @RequestParam Long userId) {
        try {
            emailDraftService.deleteDraft(userId, draftId);
            return ResponseEntity.ok(ApiResponse.success("Draft deleted successfully", "Email draft deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete email draft", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete email draft: " + e.getMessage(), "ERR_7009"));
        }
    }

    // ==================== DRAFT LİSTESİ İŞLEMLERİ ====================

    @PostMapping("/list")
    public ResponseEntity<ApiResponse<EmailDraftListResponseDto>> listDrafts(
            @RequestBody EmailDraftListRequestDto request, @RequestParam Long userId) {
        try {
            EmailDraftListResponseDto drafts = emailDraftService.listDrafts(userId, request);
            return ResponseEntity.ok(ApiResponse.success(drafts, "Email drafts retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to list email drafts", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to list email drafts: " + e.getMessage(), "ERR_7011"));
        }
    }

    // ==================== DRAFT GÖNDERME İŞLEMLERİ ====================

    @PostMapping("/{draftId}/send")
    public ResponseEntity<ApiResponse<String>> sendDraft(
            @PathVariable Long draftId, @RequestParam Long userId) {
        try {
            emailDraftService.sendDraft(userId, draftId);
            return ResponseEntity.ok(ApiResponse.success("Draft sent successfully", "Email draft sent successfully"));
        } catch (Exception e) {
            log.error("Failed to send email draft", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to send email draft: " + e.getMessage(), "ERR_7012"));
        }
    }

    // ==================== ROBOT DRAFT İŞLEMLERİ ====================

    @PostMapping("/robot")
    public ResponseEntity<ApiResponse<EmailDraftResponseDto>> createRobotDraft(
            @RequestBody EmailDraftRequestDto request, @RequestParam Long userId) {
        try {
            EmailDraftResponseDto draft = emailDraftService.createRobotDraft(userId, request);
            return ResponseEntity.ok(ApiResponse.success(draft, "Robot email draft created successfully"));
        } catch (Exception e) {
            log.error("Failed to create robot email draft", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create robot email draft: " + e.getMessage(), "ERR_7007"));
        }
    }

    @GetMapping("/robot")
    public ResponseEntity<ApiResponse<Object>> getRobotDrafts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size);
            var drafts = emailDraftService.getRobotDrafts(userId, pageable);
            
            var response = Map.of(
                "content", drafts.getContent(),
                "totalElements", (int) drafts.getTotalElements(),
                "totalPages", drafts.getTotalPages(),
                "currentPage", drafts.getNumber(),
                "pageSize", drafts.getSize()
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "Robot email drafts retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get robot email drafts", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get robot email drafts: " + e.getMessage(), "ERR_7011"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAllDrafts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size);
            var drafts = emailDraftService.getAllDrafts(userId, pageable);
            
            var response = Map.of(
                "content", drafts.getContent(),
                "totalElements", (int) drafts.getTotalElements(),
                "totalPages", drafts.getTotalPages(),
                "currentPage", drafts.getNumber(),
                "pageSize", drafts.getSize()
            );
            
            return ResponseEntity.ok(ApiResponse.success(response, "All email drafts retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get all email drafts", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get all email drafts: " + e.getMessage(), "ERR_7011"));
        }
    }

    // ==================== DRAFT İSTATİSTİKLERİ ====================

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getDraftStats(@RequestParam Long userId) {
        try {
            // Get draft statistics
            EmailDraftListRequestDto draftRequest = EmailDraftListRequestDto.builder()
                    .status("DRAFT")
                    .page(0)
                    .size(1)
                    .build();
            
            EmailDraftListResponseDto draftDrafts = emailDraftService.listDrafts(userId, draftRequest);
            
            EmailDraftListRequestDto sentRequest = EmailDraftListRequestDto.builder()
                    .status("SENT")
                    .page(0)
                    .size(1)
                    .build();
            
            EmailDraftListResponseDto sentDrafts = emailDraftService.listDrafts(userId, sentRequest);
            
            var stats = Map.of(
                "totalDrafts", draftDrafts.getTotalElements(),
                "totalSent", sentDrafts.getTotalElements(),
                "totalDraftsAll", draftDrafts.getTotalElements() + sentDrafts.getTotalElements()
            );
            
            return ResponseEntity.ok(ApiResponse.success(stats, "Draft statistics retrieved successfully"));
        } catch (Exception e) {
            log.error("Failed to get draft statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get draft statistics: " + e.getMessage(), "ERR_7011"));
        }
    }
} 