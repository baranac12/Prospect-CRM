package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "oauth_tokens")
public class OauthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_tokens_id_gen")
    @SequenceGenerator(name = "oauth_tokens_id_gen", sequenceName = "oauth_tokens_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "provider", length = Integer.MAX_VALUE)
    private String provider;

    @Column(name = "access_token", length = Integer.MAX_VALUE)
    private String accessToken;

    @Column(name = "refresh_token", length = Integer.MAX_VALUE)
    private String refreshToken;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @ColumnDefault("false")
    @Column(name = "revoked")
    private Boolean revoked;

}