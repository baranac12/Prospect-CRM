package com.prospect.crm.repository;

import com.prospect.crm.model.LeadEmailGuess;
import com.prospect.crm.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadEmailGuessRepository extends JpaRepository<LeadEmailGuess, Long> {
    List<LeadEmailGuess> findByLeadId(Lead leadId);
    List<LeadEmailGuess> findByIsVerified(boolean isVerified);
} 