package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscription_tpye")
public class SubscriptionType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_type_seq")
    @SequenceGenerator(name = "subscription_type_seq", sequenceName = "subscription_type_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "is_active")
    private Boolean isActive;

}