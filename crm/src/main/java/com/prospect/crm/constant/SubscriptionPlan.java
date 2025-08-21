package com.prospect.crm.constant;

import lombok.Getter;

@Getter
public enum SubscriptionPlan {
    
    TRIAL("trial", "Trial Plan", 0, 3, "price_trial"),
    BASIC("basic", "Basic Plan", 999, 30, "price_basic"), // $9.99/month
    PREMIUM("premium", "Premium Plan", 1999, 30, "price_premium"), // $19.99/month
    ENTERPRISE("enterprise", "Enterprise Plan", 4999, 30, "price_enterprise"); // $49.99/month
    
    private final String code;
    private final String name;
    private final int priceInCents;
    private final int durationInDays;
    private final String stripePriceId;
    
    SubscriptionPlan(String code, String name, int priceInCents, int durationInDays, String stripePriceId) {
        this.code = code;
        this.name = name;
        this.priceInCents = priceInCents;
        this.durationInDays = durationInDays;
        this.stripePriceId = stripePriceId;
    }
    
    public static SubscriptionPlan fromCode(String code) {
        for (SubscriptionPlan plan : values()) {
            if (plan.getCode().equals(code)) {
                return plan;
            }
        }
        throw new IllegalArgumentException("Unknown subscription plan: " + code);
    }
    
    public double getPriceInDollars() {
        return priceInCents / 100.0;
    }
} 