package com.prospect.crm.repository;

import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserSubsInfoRepository extends JpaRepository<UserSubsInfo, Long> {
    List<UserSubsInfo> findAllBySubsEndDateBeforeAndActiveTrue(LocalDateTime dateTime);
    List<UserSubsInfo> findByActiveTrue();
    Optional<UserSubsInfo> findByUsersId(Users usersId);
}
