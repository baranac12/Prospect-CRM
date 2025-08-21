package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.LeadEmailGuess;
import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.LeadEmailGuessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/lead-email-guess")
@PreAuthorize("hasRole('USER')")
public class LeadEmailGuessController {
    
    private final LeadEmailGuessService leadEmailGuessService;
    
    public LeadEmailGuessController(LeadEmailGuessService leadEmailGuessService) {
        this.leadEmailGuessService = leadEmailGuessService;
    }
    
    /**
     * Tüm lead email guess kayıtlarını getirir
     */
    @GetMapping
    @HasPermission(PermissionConstants.LEAD_READ)
    public ResponseEntity<ApiResponse<List<LeadEmailGuess>>> getAllLeadEmailGuesses() {
        try {
            List<LeadEmailGuess> leadEmailGuesses = leadEmailGuessService.getAll();
            
            return ResponseEntity.ok(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(true)
                    .message("Lead email guesses retrieved successfully")
                    .data(leadEmailGuesses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving lead email guesses: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(false)
                    .message("Failed to retrieve lead email guesses")
                    .build());
        }
    }
    
    /**
     * Confidence score'u belirli aralıkta olan email guess'leri getirir
     */
    @GetMapping("/score")
    @HasPermission(PermissionConstants.LEAD_READ)
    public ResponseEntity<ApiResponse<List<LeadEmailGuess>>> getLeadEmailGuessesByScore(
            @RequestParam(defaultValue = "0.0") Double start,
            @RequestParam(defaultValue = "100.0") Double end) {
        try {
            List<LeadEmailGuess> leadEmailGuesses = leadEmailGuessService.getScoreBetween(start, end);
            
            return ResponseEntity.ok(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(true)
                    .message("Lead email guesses retrieved by score successfully")
                    .data(leadEmailGuesses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving lead email guesses by score: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(false)
                    .message("Failed to retrieve lead email guesses by score")
                    .build());
        }
    }
    
    /**
     * Doğrulanmış email guess'leri getirir
     */
    @GetMapping("/validated")
    @HasPermission(PermissionConstants.LEAD_READ)
    public ResponseEntity<ApiResponse<List<LeadEmailGuess>>> getValidatedLeadEmailGuesses() {
        try {
            List<LeadEmailGuess> leadEmailGuesses = leadEmailGuessService.getValidatedTrue();
            
            return ResponseEntity.ok(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(true)
                    .message("Validated lead email guesses retrieved successfully")
                    .data(leadEmailGuesses)
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving validated lead email guesses: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(false)
                    .message("Failed to retrieve validated lead email guesses")
                    .build());
        }
    }
    
    /**
     * Yeni lead email guess oluşturur
     */
    @PostMapping
    @HasPermission(PermissionConstants.LEAD_CREATE)
    public ResponseEntity<ApiResponse<LeadEmailGuess>> createLeadEmailGuess(@Valid @RequestBody LeadEmailGuess leadEmailGuess) {
        try {
            return leadEmailGuessService.create(leadEmailGuess);
                    
        } catch (Exception e) {
            log.error("Error creating lead email guess: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<LeadEmailGuess>builder()
                    .success(false)
                    .message("Failed to create lead email guess: " + e.getMessage())
                    .build());
        }
    }
    
    /**
     * Email adresine göre lead email guess'leri getirir
     */
    @GetMapping("/email/{email}")
    @HasPermission(PermissionConstants.LEAD_READ)
    public ResponseEntity<ApiResponse<List<LeadEmailGuess>>> getLeadEmailGuessesByEmail(@PathVariable String email) {
        try {
            // Bu method'u service'e eklemek gerekiyor
            log.info("Searching lead email guesses for email: {}", email);
            
            return ResponseEntity.ok(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(true)
                    .message("Search functionality will be implemented")
                    .data(List.of())
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving lead email guesses by email: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(false)
                    .message("Failed to retrieve lead email guesses by email")
                    .build());
        }
    }
    
    /**
     * Lead ID'ye göre email guess'leri getirir
     */
    @GetMapping("/lead/{leadId}")
    @HasPermission(PermissionConstants.LEAD_READ)
    public ResponseEntity<ApiResponse<List<LeadEmailGuess>>> getLeadEmailGuessesByLeadId(@PathVariable Long leadId) {
        try {
            // Bu method'u service'e eklemek gerekiyor
            log.info("Searching lead email guesses for lead ID: {}", leadId);
            
            return ResponseEntity.ok(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(true)
                    .message("Search functionality will be implemented")
                    .data(List.of())
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving lead email guesses by lead ID: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.<List<LeadEmailGuess>>builder()
                    .success(false)
                    .message("Failed to retrieve lead email guesses by lead ID")
                    .build());
        }
    }
}