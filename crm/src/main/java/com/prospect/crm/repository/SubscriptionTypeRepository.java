package com.prospect.crm.repository;

import com.prospect.crm.model.SubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<SubscriptionType, Long> {

    Optional<SubscriptionType> findByName(String name);
    
    List<SubscriptionType> findByIsActiveTrue();
    
    Optional<SubscriptionType> findByCodeAndIsActiveTrue(String code);
    
    Optional<SubscriptionType> findByStripePriceId(String stripePriceId);
    
    Optional<SubscriptionType> findByNameAndIsActiveTrue(String name);
}