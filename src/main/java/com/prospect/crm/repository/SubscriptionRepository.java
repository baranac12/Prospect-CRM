package com.prospect.crm.repository;

import com.prospect.crm.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByStatus(String status);
    List<Subscription> findByPlanType(String planType);
    Optional<Subscription> findBySubsId(Long id);
} 