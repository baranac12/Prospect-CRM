package com.prospect.crm.service;

import com.prospect.crm.model.BounceEmail;
import com.prospect.crm.model.LeadEmailGuess;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.BounceEmailRepository;
import com.prospect.crm.repository.LeadEmailGuessRepository;
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
public class BounceEmailService {

    private final BounceEmailRepository bounceEmailRepository;
    private final LeadEmailGuessRepository leadEmailGuessRepository;
    private final UserRepository userRepository;
    private final SystemLogService systemLogService;

    /**
     * Process bounce email and update lead email guess
     */
    @Transactional
    public void processBounceEmail(String emailAddress, String bounceType, String bounceReason, 
                                 String originalMessageId, String provider, Long userId) {
        try {
            // Create bounce email record
            BounceEmail bounceEmail = createBounceEmailRecord(emailAddress, bounceType, bounceReason, 
                                                             originalMessageId, provider, userId);
            
            // Update lead email guess if it's a hard bounce
            if ("HARD_BOUNCE".equals(bounceType)) {
                updateLeadEmailGuessValidation(emailAddress);
            }
            
            // Mark bounce as processed
            bounceEmail.setProcessed(true);
            bounceEmail.setUpdatedAt(LocalDateTime.now());
            bounceEmailRepository.save(bounceEmail);
            
            systemLogService.logInfo("Bounce email processed", 
                "Email: " + emailAddress + ", Type: " + bounceType + ", Reason: " + bounceReason,
                "BounceEmailService", "processBounceEmail");
                
        } catch (Exception e) {
            systemLogService.logError("Failed to process bounce email", e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailService", "processBounceEmail");
            throw new RuntimeException("Failed to process bounce email", e);
        }
    }

    /**
     * Check if email address has recent hard bounces
     */
    @Transactional(readOnly = true)
    public boolean hasRecentHardBounces(String emailAddress) {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<BounceEmail> hardBounces = bounceEmailRepository.findHardBouncesByEmailAddress(emailAddress);
            
            return hardBounces.stream()
                    .anyMatch(bounce -> bounce.getBounceDate().isAfter(thirtyDaysAgo));
        } catch (Exception e) {
            systemLogService.logError("Failed to check hard bounces", e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailService", "hasRecentHardBounces");
            return false;
        }
    }

    /**
     * Get bounce statistics for an email address
     */
    @Transactional(readOnly = true)
    public BounceStatistics getBounceStatistics(String emailAddress) {
        try {
            List<BounceEmail> allBounces = bounceEmailRepository.findByEmailAddress(emailAddress);
            
            long hardBounces = allBounces.stream()
                    .filter(bounce -> "HARD_BOUNCE".equals(bounce.getBounceType()))
                    .count();
            
            long softBounces = allBounces.stream()
                    .filter(bounce -> "SOFT_BOUNCE".equals(bounce.getBounceType()))
                    .count();
            
            long spamReports = allBounces.stream()
                    .filter(bounce -> "SPAM".equals(bounce.getBounceType()))
                    .count();
            
            return BounceStatistics.builder()
                    .emailAddress(emailAddress)
                    .totalBounces(allBounces.size())
                    .hardBounces((int) hardBounces)
                    .softBounces((int) softBounces)
                    .spamReports((int) spamReports)
                    .hasRecentHardBounces(hasRecentHardBounces(emailAddress))
                    .build();
                    
        } catch (Exception e) {
            systemLogService.logError("Failed to get bounce statistics", e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailService", "getBounceStatistics");
            throw new RuntimeException("Failed to get bounce statistics", e);
        }
    }

    /**
     * Process all unprocessed bounces
     */
    @Transactional
    public void processAllUnprocessedBounces() {
        try {
            List<BounceEmail> unprocessedBounces = bounceEmailRepository.findByProcessedFalse();
            
            for (BounceEmail bounce : unprocessedBounces) {
                try {
                    if ("HARD_BOUNCE".equals(bounce.getBounceType())) {
                        updateLeadEmailGuessValidation(bounce.getEmailAddress());
                    }
                    
                    bounce.setProcessed(true);
                    bounce.setUpdatedAt(LocalDateTime.now());
                    bounceEmailRepository.save(bounce);
                    
                    systemLogService.logInfo("Unprocessed bounce processed", 
                        "Email: " + bounce.getEmailAddress() + ", Type: " + bounce.getBounceType(),
                        "BounceEmailService", "processAllUnprocessedBounces");
                        
                } catch (Exception e) {
                    systemLogService.logError("Failed to process individual bounce", 
                        "Email: " + bounce.getEmailAddress() + ", Error: " + e.getMessage(),
                        e.getStackTrace().toString(),
                        "BounceEmailService", "processAllUnprocessedBounces");
                }
            }
            
        } catch (Exception e) {
            systemLogService.logError("Failed to process unprocessed bounces", e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailService", "processAllUnprocessedBounces");
            throw new RuntimeException("Failed to process unprocessed bounces", e);
        }
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Create bounce email record
     */
    private BounceEmail createBounceEmailRecord(String emailAddress, String bounceType, String bounceReason,
                                               String originalMessageId, String provider, Long userId) {
        Users user = userRepository.getReferenceById(userId);
        
        BounceEmail bounceEmail = new BounceEmail();
        bounceEmail.setUserId(user);
        bounceEmail.setEmailAddress(emailAddress);
        bounceEmail.setBounceType(bounceType);
        bounceEmail.setBounceReason(bounceReason);
        bounceEmail.setOriginalMessageId(originalMessageId);
        bounceEmail.setProvider(provider);
        bounceEmail.setProcessed(false);
        bounceEmail.setBounceDate(LocalDateTime.now());
        bounceEmail.setCreatedAt(LocalDateTime.now());
        bounceEmail.setUpdatedAt(LocalDateTime.now());
        
        return bounceEmailRepository.save(bounceEmail);
    }

    /**
     * Update lead email guess validation status
     */
    private void updateLeadEmailGuessValidation(String emailAddress) {
        try {
            List<LeadEmailGuess> emailGuesses = leadEmailGuessRepository.findByGuessedEmail(emailAddress);
            
            for (LeadEmailGuess guess : emailGuesses) {
                guess.setValidated(false);
                leadEmailGuessRepository.save(guess);
                
                systemLogService.logInfo("Lead email guess validation updated", 
                    "Email: " + emailAddress + ", Lead ID: " + guess.getLeadId().getId() + ", Validated: false",
                    "BounceEmailService", "updateLeadEmailGuessValidation");
            }
            
        } catch (Exception e) {
            systemLogService.logError("Failed to update lead email guess validation", e.getMessage(), e.getStackTrace().toString(),
                "BounceEmailService", "updateLeadEmailGuessValidation");
        }
    }

    // ==================== INNER CLASSES ====================

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class BounceStatistics {
        private String emailAddress;
        private int totalBounces;
        private int hardBounces;
        private int softBounces;
        private int spamReports;
        private boolean hasRecentHardBounces;
    }
} 