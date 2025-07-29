package com.prospect.crm.repository;

import com.prospect.crm.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByStatus(String status);
    List<Subscription> findByPlanType(String planType);
} 