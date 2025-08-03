-- SEQUENCES
CREATE SEQUENCE users_id_seq START WITH 1001;
CREATE SEQUENCE roles_id_seq START WITH 1001;
CREATE SEQUENCE role_permissions_id_seq START WITH 1001;
CREATE SEQUENCE subscriptions_id_seq START WITH 1001;
CREATE SEQUENCE payments_id_seq START WITH 1001;
CREATE SEQUENCE jwt_tokens_id_seq START WITH 1001;
CREATE SEQUENCE oauth_tokens_id_seq START WITH 1001;
CREATE SEQUENCE leads_id_seq START WITH 1001;
CREATE SEQUENCE lead_email_guesses_id_seq START WITH 1001;
CREATE SEQUENCE email_drafts_id_seq START WITH 1001;
CREATE SEQUENCE email_logs_id_seq START WITH 1001;
CREATE SEQUENCE robot_instances_id_seq START WITH 1001;
CREATE SEQUENCE robot_logs_id_seq START WITH 1001;
CREATE SEQUENCE rate_limits_id_seq START WITH 1001;

-- TABLES
CREATE TABLE "users" (
  "id" INT DEFAULT nextval('users_id_seq') PRIMARY KEY,
  "email" varchar UNIQUE NOT NULL,
  "password" varchar,
  "name" varchar,
  "role_id" int,
  "subscription_id" int,
  "created_at" timestamp,
  "updated_at" timestamp
);

CREATE TABLE "roles" (
  "id" INT DEFAULT nextval('roles_id_seq') PRIMARY KEY,
  "name" varchar UNIQUE NOT NULL,
  "description" text
);

CREATE TABLE "role_permissions" (
  "id" INT DEFAULT nextval('role_permissions_id_seq') PRIMARY KEY,
  "role_id" int,
  "permission_key" varchar,
  "granted_at" timestamp
);

CREATE TABLE "subscriptions" (
  "id" INT DEFAULT nextval('subscriptions_id_seq') PRIMARY KEY,
  "name" varchar,
  "daily_limit" int,
  "price" decimal,
  "active" boolean,
  "auto_renewal" boolean,
  "start_date" timestamp,
  "end_date" timestamp
);

CREATE TABLE "payments" (
  "id" INT DEFAULT nextval('payments_id_seq') PRIMARY KEY,
  "user_id" int,
  "amount" decimal,
  "payment_date" timestamp,
  "status" varchar,
  "stripe_session_id" varchar
);

CREATE TABLE "jwt_tokens" (
  "id" INT DEFAULT nextval('jwt_tokens_id_seq') PRIMARY KEY,
  "user_id" int,
  "token" text,
  "token_type" varchar,
  "issued_at" timestamp,
  "expires_at" timestamp,
  "revoked" boolean DEFAULT false
);

CREATE TABLE "oauth_tokens" (
  "id" INT DEFAULT nextval('oauth_tokens_id_seq') PRIMARY KEY,
  "user_id" int,
  "provider" varchar,
  "access_token" text,
  "refresh_token" text,
  "issued_at" timestamp,
  "expires_at" timestamp,
  "revoked" boolean DEFAULT false
);

CREATE TABLE "leads" (
  "id" INT DEFAULT nextval('leads_id_seq') PRIMARY KEY,
  "user_id" int,
  "full_name" varchar,
  "title" varchar,
  "company_name" varchar,
  "domain" varchar,
  "linkedin_url" text,
  "source" varchar,
  "status" varchar,
  "created_at" timestamp
);

CREATE TABLE "lead_email_guesses" (
  "id" INT DEFAULT nextval('lead_email_guesses_id_seq') PRIMARY KEY,
  "lead_id" int,
  "guessed_email" varchar,
  "confidence_score" float,
  "validated" boolean,
  "created_at" timestamp
);

CREATE TABLE "email_drafts" (
  "id" INT DEFAULT nextval('email_drafts_id_seq') PRIMARY KEY,
  "user_id" int,
  "lead_id" int,
  "subject" varchar,
  "body" text,
  "created_by_robot" boolean,
  "status" varchar,
  "created_at" timestamp
);

CREATE TABLE "email_logs" (
  "id" INT DEFAULT nextval('email_logs_id_seq') PRIMARY KEY,
  "user_id" int,
  "draft_id" int,
  "recipient_email" varchar,
  "status" varchar,
  "response_received" boolean,
  "error_message" text,
  "sent_at" timestamp
);

CREATE TABLE "robot_instances" (
  "id" INT DEFAULT nextval('robot_instances_id_seq') PRIMARY KEY,
  "robot_type" varchar,
  "status" varchar,
  "launched_by" varchar,
  "launch_time" timestamp,
  "completed_time" timestamp
);

CREATE TABLE "robot_logs" (
  "id" INT DEFAULT nextval('robot_logs_id_seq') PRIMARY KEY,
  "robot_id" int,
  "user_id" int,
  "action" varchar,
  "result" varchar,
  "timestamp" timestamp,
  "log_details" text
);

CREATE TABLE "rate_limits" (
  "id" INT DEFAULT nextval('rate_limits_id_seq') PRIMARY KEY,
  "user_id" int,
  "daily_limit" int,
  "used_today" int,
  "reset_at" timestamp
);

-- FOREIGN KEYS
ALTER TABLE "users" ADD FOREIGN KEY ("role_id") REFERENCES "roles" ("id");
ALTER TABLE "users" ADD FOREIGN KEY ("subscription_id") REFERENCES "subscriptions" ("id");
ALTER TABLE "role_permissions" ADD FOREIGN KEY ("role_id") REFERENCES "roles" ("id");
ALTER TABLE "payments" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "jwt_tokens" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "oauth_tokens" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "leads" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "lead_email_guesses" ADD FOREIGN KEY ("lead_id") REFERENCES "leads" ("id");
ALTER TABLE "email_drafts" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "email_drafts" ADD FOREIGN KEY ("lead_id") REFERENCES "leads" ("id");
ALTER TABLE "email_logs" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "email_logs" ADD FOREIGN KEY ("draft_id") REFERENCES "email_drafts" ("id");
ALTER TABLE "robot_logs" ADD FOREIGN KEY ("robot_id") REFERENCES "robot_instances" ("id");
ALTER TABLE "robot_logs" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
ALTER TABLE "rate_limits" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");
