# Prospect CRM - API Dokümantasyonu

## 📋 Genel Bilgiler

**Base URL**: `http://localhost:8080`  
**API Version**: `v1`  
**Content-Type**: `application/json`  
**Authentication**: JWT Bearer Token

---

## 🔐 Authentication

### Login
```http
POST /v1/auth/login
```

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "user@example.com",
      "email": "user@example.com",
      "name": "John",
      "surname": "Doe",
      "role": "USER"
    }
  }
}
```

### Register
```http
POST /v1/auth/register
```

**Request Body:**
```json
{
  "username": "newuser@example.com",
  "email": "newuser@example.com",
  "password": "password123",
  "name": "John",
  "surname": "Doe",
  "phone": "5551234567"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 2,
    "username": "newuser@example.com",
    "email": "newuser@example.com",
    "name": "John",
    "surname": "Doe",
    "role": "USER"
  }
}
```

### Refresh Token
```http
POST /v1/auth/refresh
```

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### Logout
```http
POST /v1/auth/logout
```

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Logout successful"
}
```

---

## 🔗 OAuth Authentication

### OAuth Login
```http
POST /v1/oauth/login
```

**Request Body:**
```json
{
  "provider": "GOOGLE",
  "redirectUri": "http://localhost:3000/callback"
}
```

**Response:**
```json
{
  "success": true,
  "message": "OAuth authorization URL generated",
  "data": {
    "authorizationUrl": "https://accounts.google.com/oauth/authorize?..."
  }
}
```

### OAuth Callback
```http
GET /v1/oauth/callback?code=<authorization_code>&state=<state>
```

**Response:**
```json
{
  "success": true,
  "message": "OAuth authentication successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "email": "user@gmail.com",
      "name": "John",
      "surname": "Doe",
      "role": "USER"
    }
  }
}
```

---

## 👥 User Management

### Get All Users (Admin Only)
```http
GET /v1/users?page=0&size=10
```

**Headers:**
```
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "user@example.com",
        "email": "user@example.com",
        "name": "John",
        "surname": "Doe",
        "role": "USER",
        "isActive": true,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### Get User by ID
```http
GET /v1/users/{id}
```

**Response:**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "user@example.com",
    "email": "user@example.com",
    "name": "John",
    "surname": "Doe",
    "role": "USER",
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
}
```

### Update User
```http
PUT /v1/users/{id}
```

**Request Body:**
```json
{
  "name": "John Updated",
  "surname": "Doe Updated",
  "phone": "5559876543"
}
```

### Delete User
```http
DELETE /v1/users/{id}
```

### Check User Permissions
```http
POST /v1/users/{id}/permissions
```

**Request Body:**
```json
{
  "permissions": ["USER_CREATE", "LEAD_READ"]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Permission check completed",
  "data": {
    "hasAllPermissions": true,
    "missingPermissions": []
  }
}
```

---

## 🎯 Lead Management

### Get All Leads
```http
GET /v1/leads?page=0&size=10&status=ACTIVE
```

**Response:**
```json
{
  "success": true,
  "message": "Leads retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "companyName": "Tech Corp",
        "contactName": "Jane Smith",
        "email": "jane@techcorp.com",
        "phone": "5551234567",
        "status": "ACTIVE",
        "notes": "Interested in our services",
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### Create Lead
```http
POST /v1/leads
```

**Request Body:**
```json
{
  "companyName": "New Company",
  "contactName": "John Doe",
  "email": "john@newcompany.com",
  "phone": "5551234567",
  "notes": "Potential customer"
}
```

### Get Lead by ID
```http
GET /v1/leads/{id}
```

### Update Lead
```http
PUT /v1/leads/{id}
```

**Request Body:**
```json
{
  "status": "CONTACTED",
  "notes": "Contacted via email"
}
```

### Delete Lead
```http
DELETE /v1/leads/{id}
```

### Search Leads
```http
GET /v1/leads/search?query=tech&status=ACTIVE
```

---

## 📧 Email Management

### Send Email
```http
POST /v1/emails/send
```

**Request Body:**
```json
{
  "provider": "GMAIL",
  "fromEmail": "user@gmail.com",
  "toEmails": ["recipient@example.com"],
  "ccEmails": ["cc@example.com"],
  "bccEmails": ["bcc@example.com"],
  "subject": "Test Email",
  "body": "This is a test email",
  "contentType": "text/plain",
  "attachments": [
    {
      "filename": "document.pdf",
      "content": "base64_encoded_content",
      "contentType": "application/pdf"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Email sent successfully",
  "data": {
    "messageId": "msg_123456789",
    "sentAt": "2024-01-01T10:00:00"
  }
}
```

### Get Email List
```http
GET /v1/emails?page=0&size=10&label=INBOX
```

**Response:**
```json
{
  "success": true,
  "message": "Emails retrieved successfully",
  "data": {
    "content": [
      {
        "id": "msg_123456789",
        "from": "sender@example.com",
        "to": ["user@gmail.com"],
        "subject": "Test Email",
        "snippet": "This is a test email...",
        "date": "2024-01-01T10:00:00",
        "isRead": false,
        "labels": ["INBOX"]
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### Read Email
```http
GET /v1/emails/{id}
```

**Response:**
```json
{
  "success": true,
  "message": "Email retrieved successfully",
  "data": {
    "id": "msg_123456789",
    "from": "sender@example.com",
    "to": ["user@gmail.com"],
    "cc": ["cc@example.com"],
    "bcc": ["bcc@example.com"],
    "subject": "Test Email",
    "body": "This is a test email with full content",
    "contentType": "text/plain",
    "date": "2024-01-01T10:00:00",
    "isRead": true,
    "attachments": [
      {
        "filename": "document.pdf",
        "contentType": "application/pdf",
        "size": 1024
      }
    ]
  }
}
```

### Delete Email
```http
DELETE /v1/emails/{id}
```

### Mark Email as Read
```http
POST /v1/emails/{id}/mark-read
```

---

## 📝 Email Drafts

### Get All Drafts
```http
GET /v1/email-drafts?page=0&size=10&status=DRAFT
```

**Response:**
```json
{
  "success": true,
  "message": "Email drafts retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "leadId": 1,
        "subject": "Follow-up Email",
        "body": "Hi John, following up on our conversation...",
        "contentType": "text/plain",
        "toEmails": ["john@company.com"],
        "status": "DRAFT",
        "createdByRobot": true,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### Create Draft
```http
POST /v1/email-drafts
```

**Request Body:**
```json
{
  "leadId": 1,
  "subject": "Follow-up Email",
  "body": "Hi John, following up on our conversation...",
  "contentType": "text/plain",
  "toEmails": ["john@company.com"],
  "ccEmails": ["manager@company.com"],
  "templateName": "follow_up",
  "templateData": {
    "leadName": "John Doe",
    "companyName": "Tech Corp"
  }
}
```

### Get Robot Drafts
```http
GET /v1/email-drafts/robot?userId=1&page=0&size=10
```

### Create Robot Draft
```http
POST /v1/email-drafts/robot
```

**Request Body:**
```json
{
  "leadId": 1,
  "subject": "Automated Follow-up",
  "body": "Hi {{leadName}}, this is an automated follow-up...",
  "templateName": "automated_followup",
  "templateData": {
    "leadName": "John Doe",
    "companyName": "Tech Corp"
  }
}
```

### Send Draft
```http
POST /v1/email-drafts/{id}/send
```

**Request Body:**
```json
{
  "userId": 1
}
```

---

## 💳 Subscription Management

### Get All Subscriptions
```http
GET /v1/subscriptions?page=0&size=10
```

**Response:**
```json
{
  "success": true,
  "message": "Subscriptions retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "subscriptionType": {
          "id": 1,
          "name": "Professional",
          "code": "PRO",
          "dailyLimit": 500,
          "durationInDays": 30
        },
        "subsStartDate": "2024-01-01T00:00:00",
        "subsEndDate": "2024-02-01T00:00:00",
        "isActive": true,
        "status": "ACTIVE"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### Create Subscription
```http
POST /v1/subscriptions
```

**Request Body:**
```json
{
  "userId": 1,
  "subscriptionTypeId": 1,
  "subsStartDate": "2024-01-01T00:00:00",
  "subsEndDate": "2024-02-01T00:00:00"
}
```

### Get Subscription by ID
```http
GET /v1/subscriptions/{id}
```

### Update Subscription
```http
PUT /v1/subscriptions/{id}
```

**Request Body:**
```json
{
  "isActive": false
}
```

### Delete Subscription
```http
DELETE /v1/subscriptions/{id}
```

---

## 💰 Payment Integration

### Create Checkout Session
```http
POST /v1/payments/checkout
```

**Request Body:**
```json
{
  "subscriptionCode": "PRO",
  "userId": 1
}
```

**Response:**
```json
{
  "success": true,
  "message": "Checkout session created",
  "data": {
    "sessionId": "cs_123456789",
    "url": "https://checkout.stripe.com/pay/cs_123456789",
    "subscriptionCode": "PRO",
    "userId": 1
  }
}
```

### Payment Success Callback
```http
GET /v1/payments/success?session_id=cs_123456789
```

### Payment Cancel Callback
```http
GET /v1/payments/cancel?session_id=cs_123456789
```

### Stripe Webhook
```http
POST /v1/payments/webhook
```

**Headers:**
```
Stripe-Signature: t=1234567890,v1=signature
```

### Customer Portal
```http
GET /v1/payments/portal?userId=1
```

**Response:**
```json
{
  "success": true,
  "message": "Customer portal URL generated",
  "data": {
    "url": "https://billing.stripe.com/session/portal_123456789"
  }
}
```

---

## 📊 System Administration

### Admin Dashboard
```http
GET /v1/admin/dashboard
```

**Response:**
```json
{
  "success": true,
  "message": "Dashboard data retrieved",
  "data": {
    "totalUsers": 100,
    "activeSubscriptions": 85,
    "totalLeads": 500,
    "emailsSentToday": 250,
    "systemHealth": "HEALTHY",
    "recentErrors": [
      {
        "timestamp": "2024-01-01T10:00:00",
        "message": "Email sending failed",
        "level": "ERROR"
      }
    ]
  }
}
```

### Get System Logs
```http
GET /v1/logs?page=0&size=10&level=ERROR&type=SYSTEM
```

**Response:**
```json
{
  "success": true,
  "message": "Logs retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "level": "ERROR",
        "type": "SYSTEM",
        "message": "Database connection failed",
        "details": "Connection timeout after 30 seconds",
        "userId": null,
        "ipAddress": "192.168.1.1",
        "userAgent": "Mozilla/5.0...",
        "timestamp": "2024-01-01T10:00:00"
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  }
}
```

### Cleanup Logs
```http
GET /v1/logs/cleanup?days=30
```

**Response:**
```json
{
  "success": true,
  "message": "Log cleanup completed",
  "data": {
    "deletedCount": 1000,
    "remainingCount": 500
  }
}
```

### Emergency Log Cleanup
```http
GET /v1/logs/emergency-cleanup
```

### Log Analysis
```http
GET /v1/logs/analysis?startDate=2024-01-01&endDate=2024-01-31
```

**Response:**
```json
{
  "success": true,
  "message": "Log analysis completed",
  "data": {
    "totalLogs": 5000,
    "errorCount": 50,
    "warningCount": 100,
    "securityCount": 10,
    "topErrors": [
      {
        "message": "Email sending failed",
        "count": 25
      }
    ],
    "topUsers": [
      {
        "userId": 1,
        "logCount": 100
      }
    ]
  }
}
```

### System Info
```http
GET /v1/system/info
```

**Response:**
```json
{
  "success": true,
  "message": "System information retrieved",
  "data": {
    "version": "1.0.0",
    "javaVersion": "17.0.1",
    "springVersion": "3.1.0",
    "databaseVersion": "PostgreSQL 14.5",
    "uptime": "7 days, 3 hours, 45 minutes",
    "memoryUsage": "512MB / 2GB",
    "diskUsage": "1GB / 10GB",
    "activeConnections": 25
  }
}
```

---

## 🔧 Email Limits & Bounces

### Get Email Limit Status
```http
GET /v1/email-limits/status?userId=1
```

**Response:**
```json
{
  "success": true,
  "message": "Email limit status retrieved",
  "data": {
    "userId": 1,
    "date": "2024-01-01",
    "sentCount": 50,
    "dailyLimit": 100,
    "remaining": 50,
    "limitReached": false
  }
}
```

### Get Remaining Emails
```http
GET /v1/email-limits/remaining?userId=1
```

### Reset Daily Limit
```http
POST /v1/email-limits/reset?userId=1
```

### Get Bounce Statistics
```http
GET /v1/bounce-emails/stats?startDate=2024-01-01&endDate=2024-01-31
```

**Response:**
```json
{
  "success": true,
  "message": "Bounce statistics retrieved",
  "data": {
    "totalBounces": 25,
    "hardBounces": 15,
    "softBounces": 10,
    "topBouncedEmails": [
      {
        "email": "invalid@example.com",
        "bounceCount": 5
      }
    ],
    "bounceRate": 2.5
  }
}
```

### Process All Bounces
```http
POST /v1/bounce-emails/process-all
```

### Check Email Bounces
```http
POST /v1/bounce-emails/check
```

**Request Body:**
```json
{
  "emails": ["test@example.com", "invalid@example.com"]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Bounce check completed",
  "data": {
    "bouncedEmails": ["invalid@example.com"],
    "validEmails": ["test@example.com"]
  }
}
```

---

## 🚨 Error Responses

### Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "errorCode": "ERR_1001",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email format"
    }
  ]
}
```

### Authentication Error
```json
{
  "success": false,
  "message": "Unauthorized access",
  "errorCode": "ERR_1003"
}
```

### Resource Not Found
```json
{
  "success": false,
  "message": "User not found",
  "errorCode": "ERR_2000"
}
```

### Subscription Required
```json
{
  "success": false,
  "message": "Active subscription required",
  "errorCode": "ERR_4004"
}
```

### Email Limit Reached
```json
{
  "success": false,
  "message": "Daily email limit reached",
  "errorCode": "ERR_7017"
}
```

---

## 📋 Common Error Codes

| Code | Message | Description |
|------|---------|-------------|
| ERR_1000 | General error occurred | Genel sistem hatası |
| ERR_1001 | Validation error | Doğrulama hatası |
| ERR_1002 | Resource not found | Kaynak bulunamadı |
| ERR_1003 | Unauthorized access | Yetkisiz erişim |
| ERR_1004 | Access forbidden | Erişim yasak |
| ERR_2000 | User not found | Kullanıcı bulunamadı |
| ERR_2001 | User already exists | Kullanıcı zaten mevcut |
| ERR_2002 | Invalid credentials | Geçersiz kimlik bilgileri |
| ERR_3002 | Insufficient permissions | Yetersiz izinler |
| ERR_4004 | Payment required | Ödeme gerekli |
| ERR_7000 | Failed to send email | Email gönderimi başarısız |
| ERR_7017 | Daily email limit reached | Günlük email limiti aşıldı |

---

## 🔐 Authentication Headers

Tüm korumalı endpoint'ler için aşağıdaki header'ı kullanın:

```
Authorization: Bearer <access_token>
```

### Token Refresh
Access token süresi dolduğunda, refresh token ile yeni token alın:

```http
POST /v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 📊 Rate Limiting

API rate limiting uygulanmaktadır:
- **Authenticated Users**: 1000 requests/hour
- **Unauthenticated Users**: 100 requests/hour

Rate limit aşıldığında:
```json
{
  "success": false,
  "message": "Rate limit exceeded",
  "errorCode": "ERR_1009"
}
```

---

## 🔄 Pagination

Liste endpoint'leri pagination destekler:

```
GET /v1/users?page=0&size=10&sort=createdAt,desc
```

**Parameters:**
- `page`: Sayfa numarası (0-based)
- `size`: Sayfa başına kayıt sayısı (max: 100)
- `sort`: Sıralama (field,direction)

**Response:**
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "currentPage": 0,
  "pageSize": 10,
  "first": true,
  "last": false
}
```

---

*Son Güncelleme: 2024*  
*API Versiyon: 1.0.0* 