package com.prospect.crm.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    
    // General Errors (ERR_1000-ERR_1999)
    GENERAL_ERROR("ERR_1000", "General error occurred"),
    VALIDATION_ERROR("ERR_1001", "Validation error"),
    RESOURCE_NOT_FOUND("ERR_1002", "Resource not found"),
    UNAUTHORIZED("ERR_1003", "Unauthorized access"),
    FORBIDDEN("ERR_1004", "Access forbidden"),
    BAD_REQUEST("ERR_1005", "Bad request"),
    INTERNAL_SERVER_ERROR("ERR_1006", "Internal server error"),
    METHOD_NOT_ALLOWED("ERR_1007", "Method not allowed"),
    CONFLICT("ERR_1008", "Resource conflict"),
    RATE_LIMIT_EXCEEDED("ERR_1009", "Rate limit exceeded"),
    
    // User Related Errors (ERR_2000-ERR_2999)
    USER_NOT_FOUND("ERR_2000", "User not found"),
    USER_ALREADY_EXISTS("ERR_2001", "User already exists"),
    INVALID_CREDENTIALS("ERR_2002", "Invalid credentials"),
    USERNAME_ALREADY_EXISTS("ERR_2003", "Username already exists"),
    EMAIL_ALREADY_EXISTS("ERR_2004", "Email already exists"),
    PHONE_ALREADY_EXISTS("ERR_2005", "Phone number already exists"),
    PASSWORD_TOO_WEAK("ERR_2006", "Password is too weak"),
    ACCOUNT_DISABLED("ERR_2007", "Account is disabled"),
    ACCOUNT_LOCKED("ERR_2008", "Account is locked"),
    TOKEN_EXPIRED("ERR_2009", "Token has expired"),
    TOKEN_INVALID("ERR_2010", "Token is invalid"),
    TOKEN_REVOKED("ERR_2011", "Token has been revoked"),
    
    // Role Related Errors (ERR_3000-ERR_3999)
    ROLE_NOT_FOUND("ERR_3000", "Role not found"),
    ROLE_ALREADY_EXISTS("ERR_3001", "Role already exists"),
    INSUFFICIENT_PERMISSIONS("ERR_3002", "Insufficient permissions"),
    ROLE_IN_USE("ERR_3003", "Role is currently in use"),
    ROLE_EMPTY("ERR_3004", "Role is empty"),
    
    // Subscription Related Errors (ERR_4000-ERR_4999)
    SUBSCRIPTION_TYPE_NOT_FOUND("ERR_4000", "Subscription not found"),
    SUBSCRIPTION_TYPE_EXPIRED("ERR_4001", "Subscription has expired"),
    SUBSCRIPTION_TYPE_LIMIT_EXCEEDED("ERR_4002", "Subscription limit exceeded"),
    SUBSCRIPTION_TYPE_INACTIVE("ERR_4003", "Subscription is inactive"),
    PAYMENT_REQUIRED("ERR_4004", "Payment required"),
    SUBSCRIPTION_TYPE_ALREADY_EXISTS("ERR_4005", "Lead already exists"),
    
    // Lead Related Errors (ERR_5000-ERR_5999)
    LEAD_NOT_FOUND("ERR_5000", "Lead not found"),
    LEAD_ALREADY_EXISTS("ERR_5001", "Lead already exists"),
    INVALID_LEAD_DATA("ERR_5002", "Invalid lead data"),
    LEAD_EMAIL_GUESS_FAILED("ERR_5003", "Failed to guess lead email"),
    
    // Payment Related Errors (ERR_6000-ERR_6999)
    PAYMENT_NOT_FOUND("ERR_6000", "Payment not found"),
    PAYMENT_FAILED("ERR_6001", "Payment failed"),
    PAYMENT_ALREADY_PROCESSED("ERR_6002", "Payment already processed"),
    INVALID_PAYMENT_DATA("ERR_6003", "Invalid payment data"),
    STRIPE_SESSION_EXPIRED("ERR_6004", "Stripe session expired"),
    STRIPE_SESSION_INVALID("ERR_6005", "Invalid stripe session"),
    
    // Email Related Errors (ERR_7000-ERR_7999)
    EMAIL_SEND_FAILED("ERR_7000", "Failed to send email"),
    EMAIL_DRAFT_NOT_FOUND("ERR_7001", "Email draft not found"),
    EMAIL_LOG_NOT_FOUND("ERR_7002", "Email log not found"),
    INVALID_EMAIL_FORMAT("ERR_7003", "Invalid email format"),
    EMAIL_TEMPLATE_NOT_FOUND("ERR_7004", "Email template not found"),
    
    // Robot Related Errors (ERR_8000-ERR_8999)
    ROBOT_NOT_FOUND("ERR_8000", "Robot not found"),
    ROBOT_ALREADY_RUNNING("ERR_8001", "Robot is already running"),
    ROBOT_FAILED("ERR_8002", "Robot execution failed"),
    ROBOT_TIMEOUT("ERR_8003", "Robot execution timeout"),
    ROBOT_LOG_NOT_FOUND("ERR_8004", "Robot log not found"),
    
    // Rate Limit Related Errors (ERR_9000-ERR_9999)
    RATE_LIMIT_NOT_FOUND("ERR_9000", "Rate limit not found"),
    DAILY_LIMIT_EXCEEDED("ERR_9001", "Daily limit exceeded"),
    RATE_LIMIT_RESET_REQUIRED("ERR_9002", "Rate limit reset required"),
    
    // OAuth Related Errors (ERR_10000-ERR_10999)
    OAUTH_TOKEN_NOT_FOUND("ERR_10000", "OAuth token not found"),
    OAUTH_TOKEN_EXPIRED("ERR_10001", "OAuth token expired"),
    OAUTH_PROVIDER_ERROR("ERR_10002", "OAuth provider error"),
    OAUTH_AUTHORIZATION_FAILED("ERR_10003", "OAuth authorization failed"),
    
    // Validation Specific Errors (ERR_11000-ERR_11999)
    FIELD_REQUIRED("ERR_11000", "Field is required"),
    FIELD_TOO_SHORT("ERR_11001", "Field is too short"),
    FIELD_TOO_LONG("ERR_11002", "Field is too long"),
    INVALID_FORMAT("ERR_11003", "Invalid format"),
    INVALID_PHONE_FORMAT("ERR_11004", "Invalid phone number format"),
    INVALID_EMAIL_FORMAT_VALIDATION("ERR_11005", "Invalid email format"),
    PASSWORD_MISSING_UPPERCASE("ERR_11006", "Password must contain at least one uppercase letter"),
    PASSWORD_MISSING_LOWERCASE("ERR_11007", "Password must contain at least one lowercase letter"),
    PASSWORD_MISSING_NUMBER("ERR_11008", "Password must contain at least one number"),
    PASSWORD_MISSING_SPECIAL_CHAR("ERR_11009", "Password must contain at least one special character"),

    USER_SUBS_INFO_NOT_FOUND("ERR_12000", "User Subs not found"),
    USER_SUBS_INFO_EXPIRED("ERR_12001", "User Subs has expired"),
    USER_SUBS_INFO_INACTIVE("ERR_12003", "User Subs is inactive"),
    USER_SUBS_INFO_ACTIVE_SUBS("ERR_12005", "The user already has an active subscription"),
    USER_SUBS_INFO_ALREADY_EXISTS("ERR_12006", "Already has an active subscription");;


    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
} 