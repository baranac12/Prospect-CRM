package com.prospect.crm.repository;

import com.prospect.crm.model.Lead;
import com.prospect.crm.model.LeadEmailGuess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadEmailGuessRepository extends JpaRepository<LeadEmailGuess, Long> {
    List<LeadEmailGuess> findByConfidenceScoreBetween(Double start,Double end);
    List<LeadEmailGuess> findByValidatedTrue();
    List<LeadEmailGuess> findByLeadId(Lead lead);
    Optional<LeadEmailGuess> findByLeadIdAndGuessedEmail(Lead lead, String guessedEmail);
    List<LeadEmailGuess> findByGuessedEmail(String guessedEmail);

} 