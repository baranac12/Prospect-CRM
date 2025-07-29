package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "role_permissions")
public class RolePermission {
    @Id
    @ColumnDefault("nextval('role_permissions_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role roleId;

    @Column(name = "permission_key", length = Integer.MAX_VALUE)
    private String permissionKey;

    @Column(name = "granted_at")
    private Instant grantedAt;

}