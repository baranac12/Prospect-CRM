package com.prospect.crm.repository;

import com.prospect.crm.model.RobotInstance;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RobotInstanceRepository extends JpaRepository<RobotInstance, Long> {
    List<RobotInstance> findByUserId(Users usersId);
    List<RobotInstance> findByStatus(String status);
    List<RobotInstance> findByInstanceType(String instanceType);
} 