package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rate_limits")
public class RateLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_limit_seq")
    @SequenceGenerator(name = "rate_limit_seq", sequenceName = "rate_limits_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "used_today")
    private Integer usedToday;

    @Column(name = "reset_at")
    private LocalDateTime resetAt;

}