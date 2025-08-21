package com.prospect.crm.constant;

/**
 * Permission constants for role-based access control
 */
public class PermissionConstants {
    
    // User Management Permissions
    public static final String USER_CREATE = "user:create";
    public static final String USER_READ = "user:read";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_LIST = "user:list";
    
    // Lead Management Permissions
    public static final String LEAD_CREATE = "lead:create";
    public static final String LEAD_READ = "lead:read";
    public static final String LEAD_UPDATE = "lead:update";
    public static final String LEAD_DELETE = "lead:delete";
    public static final String LEAD_LIST = "lead:list";
    public static final String LEAD_EXPORT = "lead:export";
    public static final String LEAD_IMPORT = "lead:import";
    
    // Email Management Permissions
    public static final String EMAIL_SEND = "email:send";
    public static final String EMAIL_READ = "email:read";
    public static final String EMAIL_DELETE = "email:delete";
    public static final String EMAIL_LIST = "email:list";
    public static final String EMAIL_DRAFT_CREATE = "email:draft:create";
    public static final String EMAIL_DRAFT_READ = "email:draft:read";
    public static final String EMAIL_DRAFT_UPDATE = "email:draft:update";
    public static final String EMAIL_DRAFT_DELETE = "email:draft:delete";
    public static final String EMAIL_DRAFT_LIST = "email:draft:list";
    
    // Subscription Management Permissions
    public static final String SUBSCRIPTION_CREATE = "subscription:create";
    public static final String SUBSCRIPTION_READ = "subscription:read";
    public static final String SUBSCRIPTION_UPDATE = "subscription:update";
    public static final String SUBSCRIPTION_DELETE = "subscription:delete";
    public static final String SUBSCRIPTION_LIST = "subscription:list";
    public static final String SUBSCRIPTION_CANCEL = "subscription:cancel";
    public static final String SUBSCRIPTION_RENEW = "subscription:renew";
    
    // Payment Management Permissions
    public static final String PAYMENT_CREATE = "payment:create";
    public static final String PAYMENT_READ = "payment:read";
    public static final String PAYMENT_UPDATE = "payment:update";
    public static final String PAYMENT_DELETE = "payment:delete";
    public static final String PAYMENT_LIST = "payment:list";
    public static final String PAYMENT_REFUND = "payment:refund";
    
    // Robot Management Permissions
    public static final String ROBOT_CREATE = "robot:create";
    public static final String ROBOT_READ = "robot:read";
    public static final String ROBOT_UPDATE = "robot:update";
    public static final String ROBOT_DELETE = "robot:delete";
    public static final String ROBOT_LIST = "robot:list";
    public static final String ROBOT_START = "robot:start";
    public static final String ROBOT_STOP = "robot:stop";
    
    // System Management Permissions (Admin only)
    public static final String SYSTEM_LOG_READ = "system:log:read";
    public static final String SYSTEM_LOG_DELETE = "system:log:delete";
    public static final String SYSTEM_CONFIG_READ = "system:config:read";
    public static final String SYSTEM_CONFIG_UPDATE = "system:config:update";
    public static final String SYSTEM_BACKUP = "system:backup";
    public static final String SYSTEM_RESTORE = "system:restore";
    
    // Role Management Permissions (Admin only)
    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_READ = "role:read";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_LIST = "role:list";
    public static final String ROLE_PERMISSION_ASSIGN = "role:permission:assign";
    public static final String ROLE_PERMISSION_REVOKE = "role:permission:revoke";
    
    // OAuth Management Permissions
    public static final String OAUTH_CONNECT = "oauth:connect";
    public static final String OAUTH_DISCONNECT = "oauth:disconnect";
    public static final String OAUTH_READ = "oauth:read";
    public static final String OAUTH_LIST = "oauth:list";
    
    // Email Limit Management Permissions
    public static final String EMAIL_LIMIT_READ = "email:limit:read";
    public static final String EMAIL_LIMIT_UPDATE = "email:limit:update";
    public static final String EMAIL_LIMIT_RESET = "email:limit:reset";
    
    // Bounce Email Management Permissions
    public static final String BOUNCE_EMAIL_READ = "bounce:email:read";
    public static final String BOUNCE_EMAIL_PROCESS = "bounce:email:process";
    public static final String BOUNCE_EMAIL_DELETE = "bounce:email:delete";
    public static final String BOUNCE_EMAIL_LIST = "bounce:email:list";
    
    // Report Permissions
    public static final String REPORT_READ = "report:read";
    public static final String REPORT_CREATE = "report:create";
    public static final String REPORT_EXPORT = "report:export";
    public static final String REPORT_DELETE = "report:delete";
    
    // Dashboard Permissions
    public static final String DASHBOARD_READ = "dashboard:read";
    public static final String DASHBOARD_UPDATE = "dashboard:update";
    
    // API Permissions
    public static final String API_ACCESS = "api:access";
    public static final String API_RATE_LIMIT_BYPASS = "api:rate_limit:bypass";
} 