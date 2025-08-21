package com.prospect.crm.repository;

import com.prospect.crm.model.Lead;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByLinkedinUrl(String linkedinUrl);
    List<Lead> findAllByIsActiveTrue();
    List<Lead> findByUsersIdAndIsActiveTrue(Users usersId);
    List<Lead> findByUsersIdAndStatus(Users usersId, String status);
} 