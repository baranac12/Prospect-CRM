package com.prospect.crm.repository;

import com.prospect.crm.model.JwtToken;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    List<JwtToken> findByUserId(Users usersId);
    Optional<JwtToken> findByToken(String token);
    List<JwtToken> findByIsRevoked(boolean isRevoked);
} 