package com.prospect.crm.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for permission-based access control
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermission {
    /**
     * Required permission key
     */
    String value();
    
    /**
     * Multiple permission keys (any of them required)
     */
    String[] any() default {};
    
    /**
     * Multiple permission keys (all of them required)
     */
    String[] all() default {};
} 