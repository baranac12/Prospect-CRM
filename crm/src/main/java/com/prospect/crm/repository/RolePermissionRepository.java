package com.prospect.crm.repository;

import com.prospect.crm.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    
    /**
     * Role ID'ye göre tüm izinleri getirir
     */
    List<RolePermission> findByRoleIdId(Long roleId);
    
    /**
     * Role ID ve permission key'e göre izin kontrolü
     */
    boolean existsByRoleIdIdAndPermissionKey(Long roleId, String permissionKey);
    
    /**
     * Role ID'ye göre permission key'leri getirir
     */
    @Query("SELECT rp.permissionKey FROM RolePermission rp WHERE rp.roleId.id = :roleId")
    List<String> findPermissionKeysByRoleId(@Param("roleId") Long roleId);
    
    /**
     * Birden fazla role ID için tüm izinleri getirir
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId.id IN :roleIds")
    List<RolePermission> findByRoleIds(@Param("roleIds") List<Long> roleIds);
    
    /**
     * Role ID'ye göre izinleri siler
     */
    void deleteByRoleIdId(Long roleId);
} 