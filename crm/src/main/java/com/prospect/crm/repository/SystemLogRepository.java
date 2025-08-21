package com.prospect.crm.repository;

import com.prospect.crm.constant.LogLevel;
import com.prospect.crm.constant.LogType;
import com.prospect.crm.model.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    
    // Temel sorgular
    List<SystemLog> findByLevel(LogLevel level);
    List<SystemLog> findByType(LogType type);
    List<SystemLog> findByLevelAndType(LogLevel level, LogType type);
    
    // Tarih bazlı sorgular
    List<SystemLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<SystemLog> findByTimestampBetweenAndLevel(LocalDateTime startDate, LocalDateTime endDate, LogLevel level);
    List<SystemLog> findByTimestampBetweenAndType(LocalDateTime startDate, LocalDateTime endDate, LogType type);
    
    // Kullanıcı bazlı sorgular
    List<SystemLog> findByUserId(String userId);
    List<SystemLog> findByUserIdAndLevel(String userId, LogLevel level);
    
    // Hata logları
    List<SystemLog> findByLevelIn(List<LogLevel> levels);
    
    // API logları
    List<SystemLog> findByTypeAndHttpStatus(LogType type, Integer httpStatus);
    List<SystemLog> findByEndpointAndHttpMethod(String endpoint, String httpMethod);
    
    // Performans logları
    List<SystemLog> findByExecutionTimeGreaterThan(Long executionTime);
    
    // Sayfalama ile sorgular
    Page<SystemLog> findByLevel(LogLevel level, Pageable pageable);
    Page<SystemLog> findByType(LogType type, Pageable pageable);
    Page<SystemLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Özel sorgular
    @Query("SELECT s FROM SystemLog s WHERE s.message LIKE %:keyword% OR s.details LIKE %:keyword%")
    List<SystemLog> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(s) FROM SystemLog s WHERE s.level = :level AND s.timestamp >= :startDate")
    Long countByLevelAndDateAfter(@Param("level") LogLevel level, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT s.level, COUNT(s) FROM SystemLog s WHERE s.timestamp BETWEEN :startDate AND :endDate GROUP BY s.level")
    List<Object[]> getLogCountByLevel(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s.type, COUNT(s) FROM SystemLog s WHERE s.timestamp BETWEEN :startDate AND :endDate GROUP BY s.type")
    List<Object[]> getLogCountByType(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 1 ay sonunda hata olmayan logları silme
    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.timestamp < :cutoffDate AND s.level NOT IN ('ERROR', 'FATAL')")
    int deleteOldNonErrorLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Silinecek log sayısını sayma
    @Query("SELECT COUNT(s) FROM SystemLog s WHERE s.timestamp < :cutoffDate AND s.level NOT IN ('ERROR', 'FATAL')")
    Long countOldNonErrorLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Belirli bir tarihten önceki tüm logları silme (acil durum için)
    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.timestamp < :cutoffDate")
    int deleteAllOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Belirli bir seviyeden önceki logları silme
    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.timestamp < :cutoffDate AND s.level = :level")
    int deleteOldLogsByLevel(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("level") LogLevel level);
    
    // Belirli bir tipten önceki logları silme
    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.timestamp < :cutoffDate AND s.type = :type")
    int deleteOldLogsByType(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("type") LogType type);
    
    // Log sayım metodları
    Long countByLevel(LogLevel level);
} 