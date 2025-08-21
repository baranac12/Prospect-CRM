package com.prospect.crm.repository;

import com.prospect.crm.model.EmailLog;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    
    // Kullanıcının email loglarını tarihe göre sıralayarak getirir
    List<EmailLog> findByUserIdOrderBySentAtDesc(Users userId);
    
    // Belirli tarih aralığındaki email loglarını getirir
    List<EmailLog> findByUserIdAndSentAtBetweenOrderBySentAtDesc(Users userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Belirli durumdaki email loglarını getirir
    List<EmailLog> findByUserIdAndStatusOrderBySentAtDesc(Users userId, String status);
    
    // Başarısız email loglarını getirir (status içinde FAILED geçenler)
    List<EmailLog> findByUserIdAndStatusContainingIgnoreCaseOrderBySentAtDesc(Users userId, String status);
} 