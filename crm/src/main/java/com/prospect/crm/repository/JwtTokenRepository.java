package com.prospect.crm.repository;

import com.prospect.crm.model.JwtToken;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    
    // Token ile bulma
    Optional<JwtToken> findByAccessToken(String accessToken);
    Optional<JwtToken> findByRefreshToken(String refreshToken);
    
    // Kullanıcının aktif tokenları
    List<JwtToken> findByUserIdAndRevokedFalseAndExpiredFalse(Users userId);
    
    // Kullanıcının belirli tip tokenları
    List<JwtToken> findByUserIdAndTokenTypeAndRevokedFalseAndExpiredFalse(Users userId, String tokenType);
    
    // Süresi dolmuş tokenlar
    List<JwtToken> findByExpiresAtBefore(LocalDateTime dateTime);
    
    // Kullanıcının tüm tokenlarını iptal etme
    @Modifying
    @Query("UPDATE JwtToken t SET t.revoked = true, t.expired = true WHERE t.userId = :userId")
    void revokeAllUserTokens(@Param("userId") Users userId);
    
    // Belirli tokenı iptal etme
    @Modifying
    @Query("UPDATE JwtToken t SET t.revoked = true, t.expired = true WHERE t.accessToken = :accessToken OR t.refreshToken = :refreshToken")
    void revokeToken(@Param("accessToken") String accessToken, @Param("refreshToken") String refreshToken);
    
    // Süresi dolmuş tokenları işaretleme
    @Modifying
    @Query("UPDATE JwtToken t SET t.expired = true WHERE t.expiresAt < :dateTime AND t.expired = false")
    void markExpiredTokens(@Param("dateTime") LocalDateTime dateTime);
    
    // Kullanıcının aktif token sayısı
    Long countByUserIdAndRevokedFalseAndExpiredFalse(Users userId);
} 