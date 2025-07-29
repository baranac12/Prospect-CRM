package com.prospect.crm.repository;

import com.prospect.crm.model.EmailDraft;
import com.prospect.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailDraftRepository extends JpaRepository<EmailDraft, Integer> {
    List<EmailDraft> findByUserId(User userId);
    List<EmailDraft> findByStatus(String status);
} 