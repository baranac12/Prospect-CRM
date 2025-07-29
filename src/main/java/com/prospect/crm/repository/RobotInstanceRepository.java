package com.prospect.crm.repository;

import com.prospect.crm.model.RobotInstance;
import com.prospect.crm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RobotInstanceRepository extends JpaRepository<RobotInstance, Integer> {
    List<RobotInstance> findByUserId(User userId);
    List<RobotInstance> findByStatus(String status);
    List<RobotInstance> findByInstanceType(String instanceType);
} 