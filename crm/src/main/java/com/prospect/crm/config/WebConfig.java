package com.prospect.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Interceptor kaldırıldı - sadece JWT token kontrolü yeterli
} 