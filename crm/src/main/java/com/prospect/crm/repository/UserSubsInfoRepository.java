package com.prospect.crm.repository;

import com.prospect.crm.model.UserSubsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubsInfoRepository extends JpaRepository<UserSubsInfo, Long> {
}
