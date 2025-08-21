-- ========================================
-- DATABASE SCHEMA
-- ========================================

-- Drop existing tables if they exist (for development)
DROP TABLE IF EXISTS system_logs CASCADE;
DROP TABLE IF EXISTS email_log CASCADE;
DROP TABLE IF EXISTS robot_log CASCADE;
DROP TABLE IF EXISTS robot_instance CASCADE;
DROP TABLE IF EXISTS rate_limit CASCADE;
DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS jwt_token CASCADE;
DROP TABLE IF EXISTS oauth_token CASCADE;
DROP TABLE IF EXISTS bounce_email CASCADE;
DROP TABLE IF EXISTS daily_email_limit CASCADE;
DROP TABLE IF EXISTS email_drafts CASCADE;
DROP TABLE IF EXISTS lead_email_guess CASCADE;
DROP TABLE IF EXISTS leads CASCADE;
DROP TABLE IF EXISTS user_subs_info CASCADE;
DROP TABLE IF EXISTS subscription_type CASCADE;
DROP TABLE IF EXISTS role_permission CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- ========================================
-- ROLES
-- ========================================
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- USERS
-- ========================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT REFERENCES roles(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- PERMISSIONS
-- ========================================
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- ROLE_PERMISSION
-- ========================================
CREATE TABLE role_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, permission_id)
);

-- ========================================
-- SUBSCRIPTION_TYPE
-- ========================================
CREATE TABLE subscription_type (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration_days INTEGER NOT NULL,
    daily_email_limit INTEGER NOT NULL,
    features JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- USER_SUBS_INFO
-- ========================================
CREATE TABLE user_subs_info (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    subscription_type_id BIGINT REFERENCES subscription_type(id),
    subs_start_date TIMESTAMP NOT NULL,
    subs_end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- LEADS
-- ========================================
CREATE TABLE leads (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    title VARCHAR(255),
    domain VARCHAR(255),
    linkedin_url VARCHAR(500),
    source VARCHAR(100),
    status VARCHAR(50) DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- LEAD_EMAIL_GUESS
-- ========================================
CREATE TABLE lead_email_guess (
    id BIGSERIAL PRIMARY KEY,
    lead_id BIGINT REFERENCES leads(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    validated BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- EMAIL_DRAFTS
-- ========================================
CREATE TABLE email_drafts (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    subject VARCHAR(500) NOT NULL,
    body TEXT NOT NULL,
    cc TEXT,
    bcc TEXT,
    to_emails TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- DAILY_EMAIL_LIMIT
-- ========================================
CREATE TABLE daily_email_limit (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    sent_count INTEGER DEFAULT 0,
    limit_count INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(users_id, date)
);

-- ========================================
-- BOUNCE_EMAIL
-- ========================================
CREATE TABLE bounce_email (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    bounce_type VARCHAR(50) NOT NULL,
    bounce_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- OAUTH_TOKEN
-- ========================================
CREATE TABLE oauth_token (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    expires_at TIMESTAMP,
    scope TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- JWT_TOKEN
-- ========================================
CREATE TABLE jwt_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token_type VARCHAR(50) NOT NULL,
    access_token TEXT,
    refresh_token TEXT,
    issued_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE
);

-- ========================================
-- PAYMENTS
-- ========================================
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    subscription_type_id BIGINT REFERENCES subscription_type(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    stripe_payment_intent_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- RATE_LIMIT
-- ========================================
CREATE TABLE rate_limit (
    id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    request_count INTEGER DEFAULT 1,
    window_start TIMESTAMP NOT NULL,
    window_end TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- ROBOT_INSTANCE
-- ========================================
CREATE TABLE robot_instance (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'INACTIVE',
    last_run_time TIMESTAMP,
    next_run_time TIMESTAMP,
    configuration JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- ========================================
-- ROBOT_LOG
-- ========================================
CREATE TABLE robot_log (
    id BIGSERIAL PRIMARY KEY,
    robot_instance_id BIGINT REFERENCES robot_instance(id) ON DELETE CASCADE,
    level VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    details TEXT,
    execution_time BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- EMAIL_LOG
-- ========================================
CREATE TABLE email_log (
    id BIGSERIAL PRIMARY KEY,
    users_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    operation VARCHAR(50) NOT NULL,
    email_id VARCHAR(255),
    subject VARCHAR(500),
    recipient VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    details TEXT,
    execution_time BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- SYSTEM_LOGS
-- ========================================
CREATE TABLE system_logs (
    id BIGSERIAL PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    message VARCHAR(1000),
    details VARCHAR(4000),
    stack_trace TEXT,
    class_name VARCHAR(255),
    method_name VARCHAR(255),
    user_id VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    execution_time BIGINT,
    request_id VARCHAR(100),
    endpoint VARCHAR(500),
    http_method VARCHAR(10),
    http_status INTEGER,
    request_body TEXT,
    response_body TEXT
);

-- ========================================
-- INDEXES
-- ========================================

-- System logs indexes
CREATE INDEX idx_system_logs_timestamp ON system_logs(timestamp);
CREATE INDEX idx_system_logs_level ON system_logs(level);
CREATE INDEX idx_system_logs_type ON system_logs(type);
CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);
CREATE INDEX idx_system_logs_ip_address ON system_logs(ip_address);
CREATE INDEX idx_system_logs_level_timestamp ON system_logs(level, timestamp);
CREATE INDEX idx_system_logs_type_timestamp ON system_logs(type, timestamp);
CREATE INDEX idx_system_logs_user_timestamp ON system_logs(user_id, timestamp);
CREATE INDEX idx_system_logs_http_status ON system_logs(http_status);
CREATE INDEX idx_system_logs_endpoint ON system_logs(endpoint);
CREATE INDEX idx_system_logs_execution_time ON system_logs(execution_time);

-- Email log indexes
CREATE INDEX idx_email_log_users_id ON email_log(users_id);
CREATE INDEX idx_email_log_operation ON email_log(operation);
CREATE INDEX idx_email_log_timestamp ON email_log(created_at);
CREATE INDEX idx_email_log_status ON email_log(status);

-- Robot log indexes
CREATE INDEX idx_robot_log_instance_id ON robot_log(robot_instance_id);
CREATE INDEX idx_robot_log_level ON robot_log(level);
CREATE INDEX idx_robot_log_timestamp ON robot_log(created_at);

-- Rate limit indexes
CREATE INDEX idx_rate_limit_ip_endpoint ON rate_limit(ip_address, endpoint);
CREATE INDEX idx_rate_limit_window ON rate_limit(window_start, window_end);

-- User subscription indexes
CREATE INDEX idx_user_subs_users_id ON user_subs_info(users_id);
CREATE INDEX idx_user_subs_active ON user_subs_info(is_active);
CREATE INDEX idx_user_subs_end_date ON user_subs_info(subs_end_date);

-- Lead indexes
CREATE INDEX idx_leads_users_id ON leads(users_id);
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_company ON leads(company_name);
CREATE INDEX idx_leads_created_at ON leads(created_at);

-- OAuth token indexes
CREATE INDEX idx_oauth_token_users_id ON oauth_token(users_id);
CREATE INDEX idx_oauth_token_provider ON oauth_token(provider);
CREATE INDEX idx_oauth_token_active ON oauth_token(is_active);

-- JWT token indexes
CREATE INDEX idx_jwt_token_user_id ON jwt_token(user_id);
CREATE INDEX idx_jwt_token_type ON jwt_token(token_type);
CREATE INDEX idx_jwt_token_expires ON jwt_token(expires_at);

-- Payment indexes
CREATE INDEX idx_payments_users_id ON payments(users_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_created_at ON payments(created_at);

-- Daily email limit indexes
CREATE INDEX idx_daily_email_limit_users_date ON daily_email_limit(users_id, date);
CREATE INDEX idx_daily_email_limit_date ON daily_email_limit(date);

-- Bounce email indexes
CREATE INDEX idx_bounce_email_email ON bounce_email(email);
CREATE INDEX idx_bounce_email_type ON bounce_email(bounce_type);
CREATE INDEX idx_bounce_email_created_at ON bounce_email(created_at); 