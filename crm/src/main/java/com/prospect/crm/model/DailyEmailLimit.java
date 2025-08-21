package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "daily_email_limits", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "date"})
})
public class DailyEmailLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "daily_email_limits_seq")
    @SequenceGenerator(name = "daily_email_limits_seq", sequenceName = "daily_email_limits_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "sent_count")
    private Integer sentCount = 0;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 