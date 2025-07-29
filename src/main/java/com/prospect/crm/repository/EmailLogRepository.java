package com.prospect.crm.repository;

import com.prospect.crm.model.EmailLog;
import com.prospect.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Integer> {
    List<EmailLog> findByUserId(User userId);
    List<EmailLog> findByStatus(String status);
    List<EmailLog> findByRecipientEmail(String recipientEmail);
} 