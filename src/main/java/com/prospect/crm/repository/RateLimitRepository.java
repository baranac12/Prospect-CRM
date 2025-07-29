package com.prospect.crm.repository;

import com.prospect.crm.model.RateLimit;
import com.prospect.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimit, Integer> {
    List<RateLimit> findByUserId(User userId);
} 