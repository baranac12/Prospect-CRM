package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subs_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private boolean paymentCheck;

}
