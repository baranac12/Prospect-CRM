package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "oauth_tokens")
public class OauthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauth_tokens_id_gen")
    @SequenceGenerator(name = "oauth_tokens_id_gen", sequenceName = "oauth_tokens_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    private String provider; // GOOGLE, MICROSOFT
    private String email; // Connected email address
    private String accessToken;
    private String refreshToken;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private String scope; // OAuth scopes granted
    private String tokenType; // Bearer, etc.

    @ColumnDefault("false")
    private Boolean revoked;
    
    @ColumnDefault("false")
    private Boolean expired;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}