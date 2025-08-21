package com.prospect.crm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oauth")
public class OAuthConfig {
    
    private Google google = new Google();
    private Microsoft microsoft = new Microsoft();
    private Token token = new Token();
    
    @Data
    public static class Google {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String gmailApiUri;
    }
    
    @Data
    public static class Microsoft {
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String graphApiUri;
    }
    
    @Data
    public static class Token {
        private int expirationBuffer = 300; // 5 minutes
        private int refreshThreshold = 600; // 10 minutes
    }
} 