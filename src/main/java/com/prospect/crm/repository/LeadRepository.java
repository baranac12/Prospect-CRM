package com.prospect.crm.repository;

import com.prospect.crm.model.Lead;
import com.prospect.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Integer> {
    List<Lead> findByUserId(User userId);
    List<Lead> findByStatus(String status);
    List<Lead> findByCompanyNameContainingIgnoreCase(String companyName);
    List<Lead> findByFullNameContainingIgnoreCase(String fullName);
} 