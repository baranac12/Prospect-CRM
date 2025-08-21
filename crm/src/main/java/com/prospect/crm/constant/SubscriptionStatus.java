package com.prospect.crm.constant;

public enum SubscriptionStatus {
    ACTIVE("ACTIVE", "Aktif abonelik"),
    EXPIRED("EXPIRED", "Süresi dolmuş abonelik"),
    GRACE_PERIOD("GRACE_PERIOD", "Ek süre dönemi (3 gün)"),
    TRIAL_EXPIRED("TRIAL_EXPIRED", "Deneme süresi dolmuş"),
    NO_SUBSCRIPTION("NO_SUBSCRIPTION", "Abonelik yok");
    
    private final String code;
    private final String description;
    
    SubscriptionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
} 