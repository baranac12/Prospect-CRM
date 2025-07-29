package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "jwt_tokens")
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jwt_tokens_id_gen")
    @SequenceGenerator(name = "jwt_tokens_id_gen", sequenceName = "jwt_tokens_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "token", length = Integer.MAX_VALUE)
    private String token;

    @Column(name = "token_type", length = Integer.MAX_VALUE)
    private String tokenType;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @ColumnDefault("false")
    @Column(name = "revoked")
    private Boolean revoked;

}