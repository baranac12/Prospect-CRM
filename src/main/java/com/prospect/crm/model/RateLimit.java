package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "rate_limits")
public class RateLimit {
    @Id
    @ColumnDefault("nextval('rate_limits_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "used_today")
    private Integer usedToday;

    @Column(name = "reset_at")
    private Instant resetAt;

}