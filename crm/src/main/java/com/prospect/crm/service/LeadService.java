package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.Lead;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.LeadRepository;
import com.prospect.crm.repository.UserRepository;
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
    private final UserRepository userRepository;

    public LeadService(LeadRepository leadRepository, UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    public List<Lead> getAll() {
        return leadRepository.findAll();
    }
    
    public List<Lead> getActive() {
        return leadRepository.findAllByIsActiveTrue();
    }

    public List<Lead> getByUserId(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + userId));
        return leadRepository.findByUsersIdAndIsActiveTrue(user);
    }

    public List<Lead> getByUserIdAndStatus(Long userId, String status) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + userId));
        return leadRepository.findByUsersIdAndStatus(user, status);
    }

    public Lead getById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LEAD_NOT_FOUND + ": " + id));
    }

    public Lead getByIdAndUserId(Long id, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + userId));
        
        return leadRepository.findById(id)
                .filter(lead -> lead.getUsersId().equals(user))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LEAD_NOT_FOUND + ": " + id));
    }

    public ResponseEntity<ApiResponse<Lead>> create(Lead lead, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + userId));
        
        if (leadRepository.findByLinkedinUrl(lead.getLinkedinUrl()).isPresent()) {
            throw new ValidationException(ErrorCode.LEAD_ALREADY_EXISTS + ": " + lead.getId());
        }
        
        lead.setUsersId(user);
        lead.setCreatedAt(LocalDateTime.now());
        leadRepository.save(lead);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(lead, "Lead Created"));
    }

    public ResponseEntity<ApiResponse<Lead>> update(Long id, Lead lead, Long userId) {
        Lead savedLead = getByIdAndUserId(id, userId);
        savedLead.setCompanyName(lead.getCompanyName());
        savedLead.setDomain(lead.getDomain());
        savedLead.setSource(lead.getSource());
        savedLead.setLinkedinUrl(lead.getLinkedinUrl());
        savedLead.setTitle(lead.getTitle());
        savedLead.setFullName(lead.getFullName());
        leadRepository.save(savedLead);
        return ResponseEntity.ok(ApiResponse.success(savedLead, "Lead Updated"));
    }
    
    public ResponseEntity<ApiResponse<Void>> delete(Long id, Long userId) {
        Lead lead = getByIdAndUserId(id, userId);
        lead.setIsActive(false);
        leadRepository.save(lead);
        return ResponseEntity.ok(ApiResponse.success(null, "Lead Deleted"));
    }
}
