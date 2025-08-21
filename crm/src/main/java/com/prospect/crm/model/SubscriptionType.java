package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscription_type")
public class SubscriptionType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_type_seq")
    @SequenceGenerator(name = "subscription_type_seq", sequenceName = "subscription_type_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    private String name;
    private String code;
    private Integer dailyLimit;
    private BigDecimal price;
    private String stripePriceId;
    private Integer durationInDays;
    private String description;
    private Boolean isActive;
}