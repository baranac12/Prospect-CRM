package com.prospect.crm.repository;

import com.prospect.crm.model.EmailDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDraftRepository extends JpaRepository<EmailDraft, Long> {
} 