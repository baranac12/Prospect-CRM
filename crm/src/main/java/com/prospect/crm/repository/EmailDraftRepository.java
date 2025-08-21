package com.prospect.crm.repository;

import com.prospect.crm.model.EmailDraft;
import com.prospect.crm.model.Users;
import com.prospect.crm.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailDraftRepository extends JpaRepository<EmailDraft, Long> {
    
    // Find drafts by user
    List<EmailDraft> findByUserIdOrderByUpdatedAtDesc(Users userId);
    
    // Find drafts by user and status
    List<EmailDraft> findByUserIdAndStatusOrderByUpdatedAtDesc(Users userId, String status);
    
    // Find drafts by user with pagination
    Page<EmailDraft> findByUserIdOrderByUpdatedAtDesc(Users userId, Pageable pageable);
    
    // Find drafts by user and status with pagination
    Page<EmailDraft> findByUserIdAndStatusOrderByUpdatedAtDesc(Users userId, String status, Pageable pageable);
    
    // Find drafts by lead
    List<EmailDraft> findByLeadIdOrderByUpdatedAtDesc(Lead leadId);
    
    // Find drafts by user and lead
    List<EmailDraft> findByUserIdAndLeadIdOrderByUpdatedAtDesc(Users userId, Lead leadId);
    
    // Find drafts by user ID (for service layer)
    Page<EmailDraft> findByUserIdIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find robot drafts by user ID (for service layer)
    Page<EmailDraft> findByUserIdIdAndCreatedByRobotTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find drafts created by robot
    List<EmailDraft> findByUserIdAndCreatedByRobotTrueOrderByCreatedAtDesc(Users userId);
    
    // Find drafts by provider
    List<EmailDraft> findByUserIdAndProviderOrderByUpdatedAtDesc(Users userId, String provider);
    
    // Find drafts created after a specific date
    List<EmailDraft> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Users userId, LocalDateTime date);
    
    // Find drafts updated after a specific date
    List<EmailDraft> findByUserIdAndUpdatedAtAfterOrderByUpdatedAtDesc(Users userId, LocalDateTime date);
    
    // Count drafts by user and status
    Long countByUserIdAndStatus(Users userId, String status);
    
    // Find drafts by template name
    List<EmailDraft> findByUserIdAndTemplateNameOrderByUpdatedAtDesc(Users userId, String templateName);
    
    // Custom query for search
    @Query("SELECT ed FROM EmailDraft ed WHERE ed.userId = :userId AND " +
           "(LOWER(ed.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ed.body) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ed.toEmails) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY ed.updatedAt DESC")
    List<EmailDraft> findByUserIdAndSearchTerm(@Param("userId") Users userId, @Param("searchTerm") String searchTerm);
} 