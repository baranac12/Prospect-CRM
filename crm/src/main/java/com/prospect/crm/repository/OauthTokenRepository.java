package com.prospect.crm.repository;

import com.prospect.crm.model.OauthToken;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OauthTokenRepository extends JpaRepository<OauthToken, Long> {
    
    Optional<OauthToken> findByUserIdAndProviderAndEmail(Users userId, String provider, String email);

    @Query("SELECT ot FROM OauthToken ot WHERE ot.userId.id = :userId AND ot.provider = :provider AND ot.revoked = false AND ot.expired = false AND ot.expiresAt > :now")
    List<OauthToken> findActiveTokensByUserAndProvider(@Param("userId") Long userId, @Param("provider") String provider, @Param("now") LocalDateTime now);
    
    @Query("SELECT ot FROM OauthToken ot WHERE ot.expiresAt < :now AND ot.revoked = false")
    List<OauthToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    @Query("SELECT ot FROM OauthToken ot WHERE ot.userId.id = :userId AND ot.revoked = false AND ot.expired = false")
    List<OauthToken> findActiveTokensByUser(@Param("userId") Long userId);
} 