package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_subs_info")
public class UserSubsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersubsinfo_id_seq")
    @SequenceGenerator(name = "usersubsinfo_id_seq", sequenceName = "usersubsinfo_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_type_id")
    private SubscriptionType subscriptionTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users usersId;

    private LocalDateTime subsStartDate;
    
    private LocalDateTime subsEndDate;
    
    private Boolean paymentCheck;
    
    @ColumnDefault("true")
    private Boolean isActive = true;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
