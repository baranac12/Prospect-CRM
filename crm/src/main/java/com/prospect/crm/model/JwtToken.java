package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jwt_tokens")
public class JwtToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jwt_tokens_id_gen")
    @SequenceGenerator(name = "jwt_tokens_id_gen", sequenceName = "jwt_tokens_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "token", length = Integer.MAX_VALUE)
    private String token;

    @Column(name = "token_type", length = Integer.MAX_VALUE)
    private String tokenType;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ColumnDefault("false")
    @Column(name = "revoked")
    private Boolean revoked;

}