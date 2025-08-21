package com.prospect.crm.repository;

import com.prospect.crm.model.DailyEmailLimit;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyEmailLimitRepository extends JpaRepository<DailyEmailLimit, Long> {
    
    // Find by user and date
    Optional<DailyEmailLimit> findByUserIdAndDate(Users userId, LocalDate date);
    
    // Find by user ID and date
    Optional<DailyEmailLimit> findByUserIdIdAndDate(Long userId, LocalDate date);
    
    // Count emails sent today by user
    @Query("SELECT COALESCE(del.sentCount, 0) FROM DailyEmailLimit del WHERE del.userId.id = :userId AND del.date = :date")
    Integer countEmailsSentToday(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    // Check if user has reached daily limit
    @Query("SELECT CASE WHEN del.sentCount >= del.dailyLimit THEN true ELSE false END FROM DailyEmailLimit del WHERE del.userId.id = :userId AND del.date = :date")
    Boolean hasReachedDailyLimit(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    // Get remaining emails for today
    @Query("SELECT (del.dailyLimit - del.sentCount) FROM DailyEmailLimit del WHERE del.userId.id = :userId AND del.date = :date")
    Integer getRemainingEmails(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    // Delete records older than specified date
    @Query("DELETE FROM DailyEmailLimit del WHERE del.date < :cutoffDate")
    long deleteByDateBefore(@Param("cutoffDate") LocalDate cutoffDate);
    
    // Count records for a specific date
    @Query("SELECT COUNT(del) FROM DailyEmailLimit del WHERE del.date = :date")
    long countByDate(@Param("date") LocalDate date);
    
    // Count distinct users for a specific date
    @Query("SELECT COUNT(DISTINCT del.userId.id) FROM DailyEmailLimit del WHERE del.date = :date")
    long countDistinctUserIdByDate(@Param("date") LocalDate date);
    
    // Sum sent count for a specific date
    @Query("SELECT COALESCE(SUM(del.sentCount), 0) FROM DailyEmailLimit del WHERE del.date = :date")
    long sumSentCountByDate(@Param("date") LocalDate date);
} 