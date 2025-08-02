package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rate_limits")
public class RateLimit {
    @Id
    @ColumnDefault("nextval('rate_limits_id_seq')")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users usersId;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "used_today")
    private Integer usedToday;

    @Column(name = "reset_at")
    private LocalDateTime resetAt;

}