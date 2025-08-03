package com.prospect.crm.repository;

import com.prospect.crm.model.EmailDraft;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailDraftRepository extends JpaRepository<EmailDraft, Long> {
} 