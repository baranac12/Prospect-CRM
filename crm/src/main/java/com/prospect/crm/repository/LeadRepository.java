package com.prospect.crm.repository;

import com.prospect.crm.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByLinkedinUrl(String linkedinUrl);
    List<Lead> findAllByActiveTrue();

} 