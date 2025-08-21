package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.Lead;
import com.prospect.crm.model.LeadEmailGuess;
import com.prospect.crm.repository.LeadEmailGuessRepository;
import com.prospect.crm.repository.LeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.List;

@Slf4j
@Service
public class LeadEmailGuessService {
    private final LeadEmailGuessRepository leadEmailGuessRepository;
    private final LeadRepository leadRepository;

    public LeadEmailGuessService(LeadEmailGuessRepository getLeadEmailGuessRepository, LeadRepository leadRepository) {
        this.leadEmailGuessRepository = getLeadEmailGuessRepository;
        this.leadRepository = leadRepository;
    }

    public List<LeadEmailGuess> getAll() {
        return leadEmailGuessRepository.findAll();
    }

    public List<LeadEmailGuess> getScoreBetween(Double start, Double end) {
        return leadEmailGuessRepository.findByConfidenceScoreBetween(start, end);
    }

    public List<LeadEmailGuess> getValidatedTrue() {
        return leadEmailGuessRepository.findByValidatedTrue();
    }

    public ResponseEntity<ApiResponse<LeadEmailGuess>> create(LeadEmailGuess leadEmailGuess) {
        if (leadEmailGuessRepository.findByLeadIdAndGuessedEmail(leadEmailGuess.getLeadId(), leadEmailGuess.getGuessedEmail()).isPresent()) {
            throw new ValidationException(ErrorCode.LEAD_EMAIL_ALREADY_EXISTS + " :" + leadEmailGuess.getGuessedEmail());
        }
        leadEmailGuessRepository.save(leadEmailGuess);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(leadEmailGuess, "Lead Email Guess created"));
    }

    /**
     * Email adresine göre lead email guess'leri getirir
     */
    public List<LeadEmailGuess> getByEmail(String email) {
        return leadEmailGuessRepository.findByGuessedEmail(email);
    }

    /**
     * Lead ID'ye göre email guess'leri getirir
     */
    public List<LeadEmailGuess> getByLeadId(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));
        return leadEmailGuessRepository.findByLeadId(lead);
    }


}
