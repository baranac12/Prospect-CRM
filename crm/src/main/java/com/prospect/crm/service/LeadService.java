package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.Lead;
import com.prospect.crm.repository.LeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class LeadService {
    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public List<Lead> getAll() {
        return leadRepository.findAll();
    }
    public List<Lead> getActive() {
        return leadRepository.findAllByActiveTrue();
    }

    public Lead getById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LEAD_NOT_FOUND + ": " + id));
    }

    public ResponseEntity<ApiResponse<Lead>> create(Lead lead) {
        if (leadRepository.findByLinkedinUrl(lead.getLinkedinUrl()).isPresent()) {
            throw new ValidationException(ErrorCode.LEAD_ALREADY_EXISTS + ": " + lead.getId());
        }
        lead.setCreatedAt(LocalDateTime.now());
        leadRepository.save(lead);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(lead, "Lead Created"));
    }

    public ResponseEntity<ApiResponse<Lead>> update(Long id , Lead lead) {
        Lead savedLead = getById(id);
        savedLead.setCompanyName(lead.getCompanyName());
        savedLead.setDomain(lead.getDomain());
        savedLead.setSource(lead.getSource());
        savedLead.setLinkedinUrl(lead.getLinkedinUrl());
        savedLead.setTitle(lead.getTitle());
        savedLead.setFullName(lead.getFullName());
        leadRepository.save(savedLead);
        return ResponseEntity.ok(ApiResponse.success(savedLead, "Lead Updated"));
    }
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        Lead lead = getById(id);
        lead.setActive(false);
        leadRepository.save(lead);
        return ResponseEntity.ok(ApiResponse.success(null, "Lead Deleted"));
    }
}
