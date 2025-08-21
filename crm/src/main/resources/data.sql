-- Sample data for Prospect CRM System
-- All data is in English and follows proper naming conventions

-- ========================================
-- ROLES
-- ========================================
INSERT INTO roles (id, name, description, created_at, updated_at, is_active) VALUES 
(1001, 'USER', 'Regular user with basic permissions', NOW(), NOW(), true),
(1002, 'ADMIN', 'Administrator with full permissions', NOW(), NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- USERS
-- ========================================
-- Test users (passwords are BCrypt hashed)
-- All users: password
INSERT INTO users (id, name, surname, email, phone, username, password, role_id, created_at, updated_at, is_active) VALUES 
(1001, 'John', 'Admin', 'admin@prospectcrm.com', '+1-555-123-4567', 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1002, NOW(), NOW(), true),
(1002, 'Sarah', 'Johnson', 'sarah.johnson@prospectcrm.com', '+1-555-123-4568', 'sarah', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1001, NOW(), NOW(), true),
(1003, 'Michael', 'Chen', 'michael.chen@prospectcrm.com', '+1-555-123-4569', 'michael', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1001, NOW(), NOW(), true),
(1004, 'Emily', 'Davis', 'emily.davis@prospectcrm.com', '+1-555-123-4570', 'emily', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1001, NOW(), NOW(), true),
(1005, 'David', 'Wilson', 'david.wilson@prospectcrm.com', '+1-555-123-4571', 'david', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1001, NOW(), NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- PERMISSIONS
-- ========================================
INSERT INTO permissions (id, name, description, created_at, updated_at, is_active) VALUES 
(1001, 'LEAD_READ', 'Read leads', NOW(), NOW(), true),
(1002, 'LEAD_CREATE', 'Create leads', NOW(), NOW(), true),
(1003, 'LEAD_UPDATE', 'Update leads', NOW(), NOW(), true),
(1004, 'LEAD_DELETE', 'Delete leads', NOW(), NOW(), true),
(1005, 'EMAIL_READ', 'Read emails', NOW(), NOW(), true),
(1006, 'EMAIL_SEND', 'Send emails', NOW(), NOW(), true),
(1007, 'EMAIL_DELETE', 'Delete emails', NOW(), NOW(), true),
(1008, 'EMAIL_DRAFT_READ', 'Read email drafts', NOW(), NOW(), true),
(1009, 'EMAIL_DRAFT_CREATE', 'Create email drafts', NOW(), NOW(), true),
(1010, 'EMAIL_DRAFT_UPDATE', 'Update email drafts', NOW(), NOW(), true),
(1011, 'EMAIL_DRAFT_DELETE', 'Delete email drafts', NOW(), NOW(), true),
(1012, 'USER_READ', 'Read users', NOW(), NOW(), true),
(1013, 'USER_CREATE', 'Create users', NOW(), NOW(), true),
(1014, 'USER_UPDATE', 'Update users', NOW(), NOW(), true),
(1015, 'USER_DELETE', 'Delete users', NOW(), NOW(), true),
(1016, 'SYSTEM_LOG_READ', 'Read system logs', NOW(), NOW(), true),
(1017, 'SYSTEM_LOG_DELETE', 'Delete system logs', NOW(), NOW(), true),
(1018, 'SUBSCRIPTION_MANAGE', 'Manage subscriptions', NOW(), NOW(), true),
(1019, 'PAYMENT_READ', 'Read payments', NOW(), NOW(), true),
(1020, 'ROBOT_MANAGE', 'Manage robots', NOW(), NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- ROLE_PERMISSION
-- ========================================
INSERT INTO role_permission (id, role_id, permission_id, created_at) VALUES 
-- USER permissions
(1001, 1001, 1001, NOW()),
(1002, 1001, 1002, NOW()),
(1003, 1001, 1003, NOW()),
(1004, 1001, 1005, NOW()),
(1005, 1001, 1006, NOW()),
(1006, 1001, 1008, NOW()),
(1007, 1001, 1009, NOW()),
-- ADMIN permissions (all)
(1008, 1002, 1001, NOW()),
(1009, 1002, 1002, NOW()),
(1010, 1002, 1003, NOW()),
(1011, 1002, 1004, NOW()),
(1012, 1002, 1005, NOW()),
(1013, 1002, 1006, NOW()),
(1014, 1002, 1007, NOW()),
(1015, 1002, 1008, NOW()),
(1016, 1002, 1009, NOW()),
(1017, 1002, 1010, NOW()),
(1018, 1002, 1011, NOW()),
(1019, 1002, 1012, NOW()),
(1020, 1002, 1013, NOW()),
(1021, 1002, 1014, NOW()),
(1022, 1002, 1015, NOW()),
(1023, 1002, 1016, NOW()),
(1024, 1002, 1017, NOW()),
(1025, 1002, 1018, NOW()),
(1026, 1002, 1019, NOW()),
(1027, 1002, 1020, NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- SUBSCRIPTION_TYPE
-- ========================================
INSERT INTO subscription_type (id, name, description, price, duration_days, daily_email_limit, features, created_at, updated_at, is_active) VALUES 
(1001, 'Trial Plan', '3-day trial plan with basic features', 0.00, 3, 10, '{"features": ["Basic CRM", "Email sending", "Lead management"]}', NOW(), NOW(), true),
(1002, 'Basic Plan', 'Monthly basic plan for small teams', 9.99, 30, 100, '{"features": ["Basic CRM", "Email sending", "Lead management", "Basic reporting"]}', NOW(), NOW(), true),
(1003, 'Premium Plan', 'Monthly premium plan for growing businesses', 19.99, 30, 500, '{"features": ["Advanced CRM", "Email automation", "Lead scoring", "Advanced reporting", "API access"]}', NOW(), NOW(), true),
(1004, 'Enterprise Plan', 'Monthly enterprise plan for large organizations', 49.99, 30, 1000, '{"features": ["Full CRM", "Email automation", "Lead scoring", "Advanced reporting", "API access", "Custom integrations", "Priority support"]}', NOW(), NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- USER_SUBS_INFO
-- ========================================
INSERT INTO user_subs_info (id, users_id, subscription_type_id, subs_start_date, subs_end_date, is_active, created_at, updated_at) VALUES 
(1001, 1001, 1004, NOW(), NOW() + INTERVAL '1 year', true, NOW(), NOW()),
(1002, 1002, 1001, NOW(), NOW() + INTERVAL '3 days', true, NOW(), NOW()),
(1003, 1003, 1002, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
(1004, 1004, 1003, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW()),
(1005, 1005, 1001, NOW(), NOW() + INTERVAL '3 days', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- LEADS
-- ========================================
INSERT INTO leads (id, users_id, company_name, full_name, title, domain, linkedin_url, source, status, created_at, is_active) VALUES 
(1001, 1002, 'TechCorp Solutions', 'Alice Smith', 'CEO', 'techcorp.com', 'https://linkedin.com/in/alice-smith', 'LinkedIn', 'NEW', NOW(), true),
(1002, 1002, 'Global Industries', 'Bob Johnson', 'CTO', 'globalind.com', 'https://linkedin.com/in/bob-johnson', 'Website', 'CONTACTED', NOW(), true),
(1003, 1003, 'Innovation Labs', 'Carol White', 'VP Engineering', 'innovationlabs.com', 'https://linkedin.com/in/carol-white', 'Referral', 'QUALIFIED', NOW(), true),
(1004, 1004, 'Digital Dynamics', 'Dan Brown', 'Director', 'digitaldynamics.com', 'https://linkedin.com/in/dan-brown', 'LinkedIn', 'PROPOSAL', NOW(), true),
(1005, 1005, 'Future Systems', 'Eva Green', 'Manager', 'futuresystems.com', 'https://linkedin.com/in/eva-green', 'Website', 'NEGOTIATION', NOW(), true),
(1006, 1002, 'Smart Solutions', 'Frank Miller', 'CEO', 'smartsolutions.com', 'https://linkedin.com/in/frank-miller', 'Referral', 'CLOSED_WON', NOW(), true),
(1007, 1003, 'NextGen Tech', 'Grace Lee', 'CTO', 'nextgentech.com', 'https://linkedin.com/in/grace-lee', 'LinkedIn', 'CLOSED_LOST', NOW(), true),
(1008, 1004, 'Cloud Computing Inc', 'Henry Davis', 'VP Technology', 'cloudcomputing.com', 'https://linkedin.com/in/henry-davis', 'Website', 'NEW', NOW(), true),
(1009, 1005, 'Data Analytics Pro', 'Iris Wilson', 'Director', 'dataanalytics.com', 'https://linkedin.com/in/iris-wilson', 'Referral', 'CONTACTED', NOW(), true),
(1010, 1002, 'Mobile Apps Co', 'Jack Taylor', 'Manager', 'mobileapps.com', 'https://linkedin.com/in/jack-taylor', 'LinkedIn', 'QUALIFIED', NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- LEAD_EMAIL_GUESS
-- ========================================
INSERT INTO lead_email_guess (id, lead_id, email, validated, created_at, updated_at) VALUES 
(1001, 1001, 'alice.smith@techcorp.com', true, NOW(), NOW()),
(1002, 1001, 'a.smith@techcorp.com', false, NOW(), NOW()),
(1003, 1002, 'bob.johnson@globalind.com', true, NOW(), NOW()),
(1004, 1003, 'carol.white@innovationlabs.com', true, NOW(), NOW()),
(1005, 1004, 'dan.brown@digitaldynamics.com', true, NOW(), NOW()),
(1006, 1005, 'eva.green@futuresystems.com', true, NOW(), NOW()),
(1007, 1006, 'frank.miller@smartsolutions.com', true, NOW(), NOW()),
(1008, 1007, 'grace.lee@nextgentech.com', true, NOW(), NOW()),
(1009, 1008, 'henry.davis@cloudcomputing.com', true, NOW(), NOW()),
(1010, 1009, 'iris.wilson@dataanalytics.com', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- EMAIL_DRAFTS
-- ========================================
INSERT INTO email_drafts (id, users_id, subject, body, cc, bcc, to_emails, status, created_at, updated_at, is_active) VALUES 
(1001, 1002, 'Welcome to Prospect CRM - TechCorp Solutions', 'Dear Alice Smith,\n\nThank you for your interest in Prospect CRM. We would love to show you how our premium plan can benefit TechCorp Solutions.\n\nBest regards,\nSarah Johnson', 'sarah.johnson@prospectcrm.com', '', 'alice.smith@techcorp.com', 'DRAFT', NOW(), NOW(), true),
(1002, 1003, 'Demo Invitation - Innovation Labs', 'Dear Carol White,\n\nI am excited to schedule a demo of our CRM solution for Innovation Labs. When would be a convenient time?\n\nBest regards,\nMichael Chen', 'michael.chen@prospectcrm.com', '', 'carol.white@innovationlabs.com', 'DRAFT', NOW(), NOW(), true),
(1003, 1004, 'Proposal Follow-up - Digital Dynamics', 'Dear Dan Brown,\n\nI hope you had a chance to review our proposal for Digital Dynamics. Do you have any questions?\n\nBest regards,\nEmily Davis', 'emily.davis@prospectcrm.com', '', 'dan.brown@digitaldynamics.com', 'DRAFT', NOW(), NOW(), true),
(1004, 1005, 'Price Discussion - Future Systems', 'Dear Eva Green,\n\nI understand you have some concerns about pricing. Let me know when we can discuss this further.\n\nBest regards,\nDavid Wilson', 'david.wilson@prospectcrm.com', '', 'eva.green@futuresystems.com', 'DRAFT', NOW(), NOW(), true),
(1005, 1002, 'Thank You - Smart Solutions', 'Dear Frank Miller,\n\nThank you for choosing Prospect CRM for Smart Solutions. We look forward to a successful partnership.\n\nBest regards,\nSarah Johnson', 'sarah.johnson@prospectcrm.com', '', 'frank.miller@smartsolutions.com', 'SENT', NOW(), NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- DAILY_EMAIL_LIMIT
-- ========================================
INSERT INTO daily_email_limit (id, users_id, date, sent_count, limit_count, created_at, updated_at) VALUES 
(1001, 1002, CURRENT_DATE, 5, 10, NOW(), NOW()),
(1002, 1003, CURRENT_DATE, 8, 100, NOW(), NOW()),
(1003, 1004, CURRENT_DATE, 12, 500, NOW(), NOW()),
(1004, 1005, CURRENT_DATE, 2, 10, NOW(), NOW()),
(1005, 1002, CURRENT_DATE - INTERVAL '1 day', 7, 10, NOW(), NOW()),
(1006, 1003, CURRENT_DATE - INTERVAL '1 day', 15, 100, NOW(), NOW()),
(1007, 1004, CURRENT_DATE - INTERVAL '1 day', 25, 500, NOW(), NOW()),
(1008, 1005, CURRENT_DATE - INTERVAL '1 day', 3, 10, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- BOUNCE_EMAIL
-- ========================================
INSERT INTO bounce_email (id, email, bounce_type, bounce_reason, created_at) VALUES 
(1001, 'invalid@nonexistent.com', 'HARD', 'Address does not exist', NOW()),
(1002, 'bounce@testdomain.com', 'SOFT', 'Mailbox full', NOW()),
(1003, 'spam@blocked.com', 'HARD', 'Spam filter blocked', NOW()),
(1004, 'quota@exceeded.com', 'SOFT', 'Quota exceeded', NOW()),
(1005, 'disabled@account.com', 'HARD', 'Account disabled', NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- OAUTH_TOKEN
-- ========================================
INSERT INTO oauth_token (id, users_id, provider, access_token, refresh_token, expires_at, scope, created_at, updated_at, is_active) VALUES 
(1001, 1002, 'google', 'ya29.a0AfH6SMC...', '1//04dX...', NOW() + INTERVAL '1 hour', 'openid profile email https://www.googleapis.com/auth/gmail.send', NOW(), NOW(), true),
(1002, 1003, 'microsoft', 'EwBwA8l6BAAU...', 'M.R3_BAY...', NOW() + INTERVAL '1 hour', 'openid profile email https://graph.microsoft.com/Mail.Send', NOW(), NOW(), true),
(1003, 1004, 'google', 'ya29.a0AfH6SMC...', '1//04dX...', NOW() + INTERVAL '1 hour', 'openid profile email https://www.googleapis.com/auth/gmail.send', NOW(), NOW(), true),
(1004, 1005, 'microsoft', 'EwBwA8l6BAAU...', 'M.R3_BAY...', NOW() + INTERVAL '1 hour', 'openid profile email https://graph.microsoft.com/Mail.Send', NOW(), NOW(), true),
(1005, 1002, 'outlook', 'EwBwA8l6BAAU...', 'M.R3_BAY...', NOW() - INTERVAL '1 hour', 'openid profile email https://graph.microsoft.com/Mail.Send', NOW(), NOW(), false)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- JWT_TOKEN
-- ========================================
INSERT INTO jwt_token (id, user_id, token_type, access_token, refresh_token, issued_at, expires_at, revoked, expired) VALUES 
(1001, 1001, 'ACCESS', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...', NULL, NOW(), NOW() + INTERVAL '2 hours', false, false),
(1002, 1001, 'REFRESH', NULL, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...', NOW(), NOW() + INTERVAL '8 hours', false, false),
(1003, 1002, 'ACCESS', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...', NULL, NOW(), NOW() + INTERVAL '2 hours', false, false),
(1004, 1002, 'REFRESH', NULL, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...', NOW(), NOW() + INTERVAL '8 hours', false, false),
(1005, 1003, 'ACCESS', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...', NULL, NOW(), NOW() + INTERVAL '2 hours', false, false)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- PAYMENTS
-- ========================================
INSERT INTO payments (id, users_id, subscription_type_id, amount, currency, status, payment_method, stripe_payment_intent_id, created_at, updated_at) VALUES 
(1001, 1003, 1002, 9.99, 'USD', 'SUCCEEDED', 'card', 'pi_3NxYzK2eZvKYlo2C1gQJ8X9Y', NOW(), NOW()),
(1002, 1004, 1003, 19.99, 'USD', 'SUCCEEDED', 'card', 'pi_3NxYzK2eZvKYlo2C1gQJ8X9Z', NOW(), NOW()),
(1003, 1002, 1001, 0.00, 'USD', 'SUCCEEDED', 'trial', 'pi_3NxYzK2eZvKYlo2C1gQJ8X9A', NOW(), NOW()),
(1004, 1005, 1001, 0.00, 'USD', 'SUCCEEDED', 'trial', 'pi_3NxYzK2eZvKYlo2C1gQJ8X9B', NOW(), NOW()),
(1005, 1001, 1004, 49.99, 'USD', 'SUCCEEDED', 'card', 'pi_3NxYzK2eZvKYlo2C1gQJ8X9C', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- RATE_LIMIT
-- ========================================
INSERT INTO rate_limit (id, ip_address, endpoint, request_count, window_start, window_end, created_at, updated_at) VALUES 
(1001, '192.168.1.100', '/v1/leads', 15, NOW(), NOW() + INTERVAL '1 hour', NOW(), NOW()),
(1002, '192.168.1.101', '/v1/emails', 25, NOW(), NOW() + INTERVAL '1 hour', NOW(), NOW()),
(1003, '192.168.1.102', '/v1/email-drafts', 8, NOW(), NOW() + INTERVAL '1 hour', NOW(), NOW()),
(1004, '192.168.1.103', '/v1/users', 5, NOW(), NOW() + INTERVAL '1 hour', NOW(), NOW()),
(1005, '192.168.1.104', '/v1/admin', 12, NOW(), NOW() + INTERVAL '1 hour', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- ROBOT_INSTANCE
-- ========================================
INSERT INTO robot_instance (id, name, description, status, last_run_time, next_run_time, configuration, created_at, updated_at, is_active) VALUES 
(1001, 'EmailDraftGenerator', 'Generates email drafts for users', 'ACTIVE', NOW() - INTERVAL '30 minutes', NOW() + INTERVAL '30 minutes', '{"frequency": "hourly", "max_drafts": 5}', NOW(), NOW(), true),
(1002, 'LeadScorer', 'Scores leads based on various criteria', 'ACTIVE', NOW() - INTERVAL '1 hour', NOW() + INTERVAL '1 hour', '{"frequency": "hourly", "scoring_rules": ["engagement", "company_size"]}', NOW(), NOW(), true),
(1003, 'EmailValidator', 'Validates email addresses', 'ACTIVE', NOW() - INTERVAL '15 minutes', NOW() + INTERVAL '15 minutes', '{"frequency": "15min", "validation_method": "smtp"}', NOW(), NOW(), true),
(1004, 'SubscriptionChecker', 'Checks subscription status', 'ACTIVE', NOW() - INTERVAL '2 hours', NOW() + INTERVAL '2 hours', '{"frequency": "2hours", "grace_period": 3}', NOW(), NOW(), true),
(1005, 'LogCleaner', 'Cleans old system logs', 'INACTIVE', NOW() - INTERVAL '1 day', NULL, '{"frequency": "daily", "retention_days": 30}', NOW(), NOW(), true)
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- ROBOT_LOG
-- ========================================
INSERT INTO robot_log (id, robot_instance_id, level, message, details, execution_time, created_at) VALUES 
(1001, 1001, 'INFO', 'Generated 5 email drafts for user 1002', 'Successfully created drafts for TechCorp Solutions lead', 15000, NOW() - INTERVAL '30 minutes'),
(1002, 1002, 'INFO', 'Scored 10 leads for user 1003', 'Applied scoring algorithm to Innovation Labs leads', 8000, NOW() - INTERVAL '1 hour'),
(1003, 1003, 'INFO', 'Validated 25 email addresses', 'Checked email validity using SMTP verification', 5000, NOW() - INTERVAL '15 minutes'),
(1004, 1004, 'INFO', 'Checked subscription status for 50 users', 'Verified active subscriptions and trial periods', 12000, NOW() - INTERVAL '2 hours'),
(1005, 1005, 'INFO', 'Cleaned old logs - deleted 150 records', 'Removed logs older than 30 days', 25000, NOW() - INTERVAL '1 day')
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- EMAIL_LOG
-- ========================================
INSERT INTO email_log (id, users_id, operation, email_id, subject, recipient, status, details, execution_time, created_at) VALUES 
(1001, 1002, 'SEND', 'msg_001', 'Welcome to Prospect CRM', 'alice.smith@techcorp.com', 'SUCCESS', 'Email sent successfully via Gmail API', 2500, NOW() - INTERVAL '1 hour'),
(1002, 1003, 'SEND', 'msg_002', 'Demo Invitation', 'carol.white@innovationlabs.com', 'SUCCESS', 'Email sent successfully via Outlook API', 1800, NOW() - INTERVAL '2 hours'),
(1003, 1004, 'SEND', 'msg_003', 'Proposal Follow-up', 'dan.brown@digitaldynamics.com', 'SUCCESS', 'Email sent successfully via Gmail API', 2200, NOW() - INTERVAL '3 hours'),
(1004, 1005, 'SEND', 'msg_004', 'Price Discussion', 'eva.green@futuresystems.com', 'SUCCESS', 'Email sent successfully via Outlook API', 1900, NOW() - INTERVAL '4 hours'),
(1005, 1002, 'SEND', 'msg_005', 'Thank You', 'frank.miller@smartsolutions.com', 'SUCCESS', 'Email sent successfully via Gmail API', 2100, NOW() - INTERVAL '5 hours'),
(1006, 1002, 'READ', 'msg_006', 'Re: Welcome to Prospect CRM', 'alice.smith@techcorp.com', 'SUCCESS', 'Email read successfully', 500, NOW() - INTERVAL '30 minutes'),
(1007, 1003, 'DELETE', 'msg_007', 'Old Email', 'old.email@test.com', 'SUCCESS', 'Email deleted successfully', 300, NOW() - INTERVAL '1 day'),
(1008, 1004, 'TEMPLATE_RENDER', 'msg_008', 'Email Template', 'template@test.com', 'SUCCESS', 'Template rendered successfully', 800, NOW() - INTERVAL '6 hours')
ON CONFLICT (id) DO NOTHING;

-- ========================================
-- SYSTEM_LOGS
-- ========================================
INSERT INTO system_logs (id, level, type, message, details, stack_trace, class_name, method_name, user_id, ip_address, user_agent, timestamp, execution_time, request_id, endpoint, http_method, http_status, request_body, response_body) VALUES 
(1001, 'INFO', 'SYSTEM', 'Application started successfully', 'Spring Boot application initialized', NULL, 'com.prospect.crm.CrmApplication', 'main', NULL, NULL, NULL, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1002, 'INFO', 'SECURITY', 'User login successful', 'User 1002 logged in successfully', NULL, 'com.prospect.crm.security.JwtAuthenticationFilter', 'doFilterInternal', '1002', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', NOW(), 150, 'req_001', '/v1/auth/login', 'POST', 200, '{"email":"sarah.johnson@prospectcrm.com","password":"***"}', '{"success":true,"data":{"token":"..."}}'),
(1003, 'WARN', 'API', 'Rate limit exceeded', 'User 1003 exceeded rate limit for endpoint /v1/emails', NULL, 'com.prospect.crm.interceptor.RateLimitInterceptor', 'preHandle', '1003', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', NOW(), 50, 'req_002', '/v1/emails', 'GET', 429, NULL, '{"error":"Rate limit exceeded"}'),
(1004, 'ERROR', 'ERROR', 'Database connection failed', 'Failed to connect to PostgreSQL database', 'java.sql.SQLException: Connection refused', 'com.prospect.crm.config.DatabaseConfig', 'dataSource', NULL, NULL, NULL, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1005, 'INFO', 'BUSINESS', 'Lead created successfully', 'New lead created for TechCorp Solutions', NULL, 'com.prospect.crm.service.LeadService', 'createLead', '1002', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', NOW(), 800, 'req_003', '/v1/leads', 'POST', 201, '{"companyName":"TechCorp Solutions","fullName":"Alice Smith"}', '{"success":true,"data":{"id":1001}}'),
(1006, 'INFO', 'PERFORMANCE', 'Email sending performance', 'Email sent in 2500ms', NULL, 'com.prospect.crm.service.EmailService', 'sendEmail', '1002', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', NOW(), 2500, 'req_004', '/v1/emails/send', 'POST', 200, '{"to":"alice.smith@techcorp.com","subject":"Welcome"}', '{"success":true}'),
(1007, 'INFO', 'DATABASE', 'Query executed successfully', 'SELECT * FROM leads WHERE users_id = 1002', NULL, 'com.prospect.crm.repository.LeadRepository', 'findByUsersId', '1002', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', NOW(), 120, 'req_005', '/v1/leads', 'GET', 200, NULL, '{"success":true,"data":[...]}'),
(1008, 'INFO', 'EXTERNAL_SERVICE', 'Stripe API call successful', 'Payment processed successfully', NULL, 'com.prospect.crm.service.StripeService', 'processPayment', '1003', '192.168.1.102', 'Mozilla/5.0 (Linux x86_64) AppleWebKit/537.36', NOW(), 1500, 'req_006', '/v1/payments/process', 'POST', 200, '{"amount":9.99,"currency":"USD"}', '{"success":true,"paymentId":"pi_..."}'),
(1009, 'INFO', 'AUDIT', 'User role changed', 'User 1004 role changed from USER to ADMIN', NULL, 'com.prospect.crm.service.UserService', 'updateUserRole', '1001', '192.168.1.103', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', NOW(), 300, 'req_007', '/v1/users/1004/role', 'PUT', 200, '{"role":"ADMIN"}', '{"success":true}'),
(1010, 'WARN', 'SYSTEM', 'Low disk space', 'Disk space usage is at 85%', NULL, 'com.prospect.crm.scheduler.SystemHealthScheduler', 'checkDiskSpace', NULL, NULL, NULL, NOW(), NULL, NULL, NULL, NULL, NULL, NULL, NULL)
ON CONFLICT (id) DO NOTHING; 