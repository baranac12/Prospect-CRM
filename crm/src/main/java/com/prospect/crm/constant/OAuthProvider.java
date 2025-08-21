package com.prospect.crm.constant;

public enum OAuthProvider {
    GOOGLE("google", "Gmail"),
    MICROSOFT("microsoft", "Outlook");
    
    private final String code;
    private final String displayName;
    
    OAuthProvider(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static OAuthProvider fromCode(String code) {
        for (OAuthProvider provider : values()) {
            if (provider.getCode().equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OAuth provider: " + code);
    }
} 