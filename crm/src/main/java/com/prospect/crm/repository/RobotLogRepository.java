package com.prospect.crm.repository;

import com.prospect.crm.model.RobotLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RobotLogRepository extends JpaRepository<RobotLog, Long> {
} 