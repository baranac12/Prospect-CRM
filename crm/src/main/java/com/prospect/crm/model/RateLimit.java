package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rate_limit")
public class RateLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rate_limit_seq")
    @SequenceGenerator(name = "rate_limit_seq", sequenceName = "rate_limits_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    private Integer dailyLimit;
    private Integer usedToday;
    private LocalDateTime resetAt;
}