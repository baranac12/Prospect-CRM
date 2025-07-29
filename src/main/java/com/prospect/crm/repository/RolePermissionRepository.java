package com.prospect.crm.repository;

import com.prospect.crm.model.RolePermission;
import com.prospect.crm.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    List<RolePermission> findByRoleId(Role roleId);
    List<RolePermission> findByPermission(String permission);
} 