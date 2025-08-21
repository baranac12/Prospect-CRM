package com.prospect.crm.repository;

import com.prospect.crm.model.BounceEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BounceEmailRepository extends JpaRepository<BounceEmail, Long> {
    
    // Find by email address
    List<BounceEmail> findByEmailAddress(String emailAddress);
    
    // Find unprocessed bounces
    List<BounceEmail> findByProcessedFalse();
    
    // Find by user and unprocessed
    List<BounceEmail> findByUserIdIdAndProcessedFalse(Long userId);
    
    // Find by bounce type
    List<BounceEmail> findByBounceType(String bounceType);
    
    // Find by provider
    List<BounceEmail> findByProvider(String provider);
    
    // Find by original message ID
    Optional<BounceEmail> findByOriginalMessageId(String originalMessageId);
    
    // Find bounces after a specific date
    List<BounceEmail> findByBounceDateAfter(LocalDateTime date);
    
    // Count bounces by email address
    @Query("SELECT COUNT(be) FROM BounceEmail be WHERE be.emailAddress = :emailAddress")
    long countByEmailAddress(@Param("emailAddress") String emailAddress);
    
    // Find hard bounces for an email address
    @Query("SELECT be FROM BounceEmail be WHERE be.emailAddress = :emailAddress AND be.bounceType = 'HARD_BOUNCE'")
    List<BounceEmail> findHardBouncesByEmailAddress(@Param("emailAddress") String emailAddress);
    
    // Find recent bounces for an email address
    @Query("SELECT be FROM BounceEmail be WHERE be.emailAddress = :emailAddress AND be.bounceDate > :since")
    List<BounceEmail> findRecentBouncesByEmailAddress(@Param("emailAddress") String emailAddress, @Param("since") LocalDateTime since);
} 