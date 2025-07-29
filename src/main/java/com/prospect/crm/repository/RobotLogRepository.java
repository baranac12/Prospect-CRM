package com.prospect.crm.repository;

import com.prospect.crm.model.RobotLog;
import com.prospect.crm.model.RobotInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RobotLogRepository extends JpaRepository<RobotLog, Integer> {
    List<RobotLog> findByRobotInstanceId(RobotInstance robotInstanceId);
    List<RobotLog> findByLogLevel(String logLevel);
    List<RobotLog> findByMessageContainingIgnoreCase(String message);
} 