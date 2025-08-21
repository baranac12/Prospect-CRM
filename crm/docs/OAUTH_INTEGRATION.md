# OAuth Entegrasyonu - Gmail ve Outlook

## 📋 İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [Desteklenen Provider'lar](#desteklenen-providerlar)
3. [Konfigürasyon](#konfigürasyon)
4. [API Endpoint'leri](#api-endpointleri)
5. [Kullanım Örnekleri](#kullanım-örnekleri)
6. [Güvenlik](#güvenlik)
7. [Troubleshooting](#troubleshooting)
8. [Best Practices](#best-practices)

---

## 🔐 Genel Bakış

Prospect CRM sistemi, Gmail ve Outlook email hesaplarını OAuth2 protokolü ile güvenli bir şekilde bağlamanızı sağlar. Bu entegrasyon sayesinde:

- **Güvenli Bağlantı:** Şifre paylaşmadan email hesaplarına erişim
- **Token Yönetimi:** Otomatik token yenileme ve yönetimi
- **Email Gönderimi:** Gmail API ve Microsoft Graph API ile email gönderimi
- **Çoklu Hesap:** Birden fazla email hesabını aynı anda bağlama

### Avantajları

- ✅ **Güvenli:** OAuth2 protokolü ile güvenli kimlik doğrulama
- ✅ **Kolay:** Basit API endpoint'leri ile kolay entegrasyon
- ✅ **Otomatik:** Token yenileme ve yönetimi otomatik
- ✅ **Çoklu:** Gmail ve Outlook desteği
- ✅ **Monitoring:** Detaylı loglama ve monitoring

---

## 🏢 Desteklenen Provider'lar

### 1. Google Gmail
- **Provider Code:** `google`
- **API:** Gmail API v1
- **Scopes:** 
  - `https://www.googleapis.com/auth/gmail.send`
  - `https://www.googleapis.com/auth/gmail.readonly`
  - `openid`, `profile`, `email`

### 2. Microsoft Outlook
- **Provider Code:** `microsoft`
- **API:** Microsoft Graph API v1.0
- **Scopes:**
  - `https://graph.microsoft.com/Mail.Send`
  - `https://graph.microsoft.com/Mail.Read`
  - `openid`, `profile`, `email`, `offline_access`

---

## ⚙️ Konfigürasyon

### 1. Application Properties

```properties
# OAuth2 Configuration
# Gmail OAuth2
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
spring.security.oauth2.client.registration.google.scope=openid,profile,email,https://www.googleapis.com/auth/gmail.send,https://www.googleapis.com/auth/gmail.readonly
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/v1/oauth/callback/google

# Microsoft OAuth2 (Outlook)
spring.security.oauth2.client.registration.microsoft.client-id=your-microsoft-client-id
spring.security.oauth2.client.registration.microsoft.client-secret=your-microsoft-client-secret
spring.security.oauth2.client.registration.microsoft.scope=openid,profile,email,offline_access,https://graph.microsoft.com/Mail.Send,https://graph.microsoft.com/Mail.Read
spring.security.oauth2.client.registration.microsoft.redirect-uri={baseUrl}/v1/oauth/callback/microsoft

# OAuth2 Provider Configuration
oauth.google.authorization-uri=https://accounts.google.com/o/oauth2/v2/auth
oauth.google.token-uri=https://oauth2.googleapis.com/token
oauth.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
oauth.google.gmail-api-uri=https://gmail.googleapis.com/gmail/v1

oauth.microsoft.authorization-uri=https://login.microsoftonline.com/common/oauth2/v2.0/authorize
oauth.microsoft.token-uri=https://login.microsoftonline.com/common/oauth2/v2.0/token
oauth.microsoft.user-info-uri=https://graph.microsoft.com/v1.0/me
oauth.microsoft.graph-api-uri=https://graph.microsoft.com/v1.0

# OAuth Token Settings
oauth.token.expiration-buffer=300
oauth.refresh.threshold=600
```

### 2. Google Cloud Console Setup

1. **Google Cloud Console'a gidin:** https://console.cloud.google.com/
2. **Yeni proje oluşturun** veya mevcut projeyi seçin
3. **OAuth consent screen** oluşturun
4. **Credentials > OAuth 2.0 Client IDs** oluşturun
5. **Authorized redirect URIs** ekleyin:
   - `http://localhost:8080/v1/oauth/callback/google` (development)
   - `https://yourdomain.com/v1/oauth/callback/google` (production)

### 3. Microsoft Azure Setup

1. **Azure Portal'a gidin:** https://portal.azure.com/
2. **App registrations** oluşturun
3. **Authentication** ayarlarını yapın
4. **Redirect URIs** ekleyin:
   - `http://localhost:8080/v1/oauth/callback/microsoft` (development)
   - `https://yourdomain.com/v1/oauth/callback/microsoft` (production)
5. **API permissions** ekleyin:
   - `Mail.Send`
   - `Mail.Read`

---

## 🌐 API Endpoint'leri

### OAuth Endpoints

#### 1. OAuth Provider'larını Listele
```http
GET /v1/oauth/providers
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "code": "google",
      "name": "Gmail",
      "description": "Google Gmail OAuth2"
    },
    {
      "code": "microsoft",
      "name": "Outlook",
      "description": "Microsoft Outlook OAuth2"
    }
  ],
  "message": "OAuth providers retrieved successfully"
}
```

#### 2. OAuth Authorization URL Oluştur
```http
POST /v1/oauth/authorize
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "provider": "google",
  "email": "user@gmail.com",
  "redirectUri": "http://localhost:3000/oauth/callback"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?client_id=...",
    "state": "random-state-string",
    "provider": "google"
  },
  "message": "Authorization URL created successfully"
}
```

#### 3. OAuth Callback
```http
GET /v1/oauth/callback/google?code=authorization_code&state=state&userId=1001
```

**Response:**
```json
{
  "success": true,
  "data": {
    "provider": "google",
    "email": "user@gmail.com",
    "status": "CONNECTED",
    "connectedAt": "2024-01-01T10:00:00",
    "expiresAt": "2024-01-01T12:00:00",
    "scopes": ["openid", "profile", "email", "https://www.googleapis.com/auth/gmail.send"],
    "accessToken": "ya29.a0...",
    "refreshToken": "1//04..."
  },
  "message": "OAuth connection successful"
}
```

#### 4. Bağlı Hesapları Listele
```http
GET /v1/oauth/accounts?userId=1001
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "provider": "google",
      "email": "user@gmail.com",
      "status": "CONNECTED",
      "connectedAt": "2024-01-01T10:00:00",
      "expiresAt": "2024-01-01T12:00:00",
      "scopes": ["openid", "profile", "email", "https://www.googleapis.com/auth/gmail.send"]
    },
    {
      "provider": "microsoft",
      "email": "user@outlook.com",
      "status": "CONNECTED",
      "connectedAt": "2024-01-01T11:00:00",
      "expiresAt": "2024-01-01T13:00:00",
      "scopes": ["openid", "profile", "email", "https://graph.microsoft.com/Mail.Send"]
    }
  ],
  "message": "Connected accounts retrieved successfully"
}
```

#### 5. Hesap Bağlantısını Kes
```http
DELETE /v1/oauth/disconnect?userId=1001&provider=google&email=user@gmail.com
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "success": true,
  "data": "Account disconnected successfully",
  "message": "Account disconnected successfully"
}
```

#### 6. OAuth Durumu Kontrol Et
```http
GET /v1/oauth/status?userId=1001
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": 1001,
    "connectedAccounts": 2,
    "accounts": [...],
    "hasGmail": true,
    "hasOutlook": true
  },
  "message": "OAuth status retrieved successfully"
}
```

### Email Endpoints

#### 1. Email Gönder
```http
POST /v1/emails/send
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "provider": "google",
  "fromEmail": "user@gmail.com",
  "toEmails": ["recipient@example.com"],
  "ccEmails": ["cc@example.com"],
  "bccEmails": ["bcc@example.com"],
  "subject": "Test Email",
  "body": "<h1>Hello World</h1><p>This is a test email.</p>",
  "contentType": "text/html",
  "attachments": [
    {
      "fileName": "document.pdf",
      "contentType": "application/pdf",
      "base64Content": "JVBERi0xLjQK..."
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "data": "Email sent successfully",
  "message": "Email sent successfully"
}
```

#### 2. SMTP ile Email Gönder (Fallback)
```http
POST /v1/emails/send-smtp
Authorization: Bearer {jwt_token}
Content-Type: application/x-www-form-urlencoded

fromEmail=user@gmail.com&password=app_password&toEmails=recipient@example.com&subject=Test&body=Hello&contentType=text/plain
```

#### 3. Email Template Render Et
```http
POST /v1/emails/render-template?templateName=welcome
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "John Doe",
  "username": "johndoe"
}
```

**Response:**
```json
{
  "success": true,
  "data": "<html><body><h1>Hoş Geldiniz!</h1><p>Merhaba John Doe,</p>...</body></html>",
  "message": "Template rendered successfully"
}
```

#### 4. Email Template'lerini Listele
```http
GET /v1/emails/templates
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "name": "welcome",
      "description": "Hoş geldiniz email template'i"
    },
    {
      "name": "lead_followup",
      "description": "Lead takip email template'i"
    }
  ],
  "message": "Email templates retrieved successfully"
}
```

#### 5. Email Durumu Kontrol Et
```http
GET /v1/emails/status?userId=1001
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": 1001,
    "canSendEmail": true,
    "supportedProviders": ["Gmail", "Outlook"],
    "message": "Email service is available"
  },
  "message": "Email status retrieved successfully"
}
```

---

## 💡 Kullanım Örnekleri

### 1. Gmail Hesabı Bağlama

```javascript
// 1. Authorization URL al
const response = await fetch('/v1/oauth/authorize', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + jwtToken,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    provider: 'google',
    email: 'user@gmail.com'
  })
});

const { data } = await response.json();

// 2. Kullanıcıyı OAuth sayfasına yönlendir
window.location.href = data.authorizationUrl;

// 3. Callback'te token'ları al
// Bu otomatik olarak /v1/oauth/callback/google endpoint'inde işlenir
```

### 2. Email Gönderme

```javascript
// Gmail ile email gönder
const emailResponse = await fetch('/v1/emails/send', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + jwtToken,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    provider: 'google',
    fromEmail: 'user@gmail.com',
    toEmails: ['recipient@example.com'],
    subject: 'Lead Takip',
    body: '<h1>Merhaba</h1><p>Lead takip email'i.</p>',
    contentType: 'text/html'
  })
});
```

### 3. Template ile Email Gönderme

```javascript
// Template render et
const templateResponse = await fetch('/v1/emails/render-template?templateName=lead_followup', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + jwtToken,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    leadName: 'John Doe',
    companyName: 'ABC Company',
    senderName: 'Sales Team'
  })
});

const { data: emailBody } = await templateResponse.json();

// Email gönder
await fetch('/v1/emails/send', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + jwtToken,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    provider: 'google',
    fromEmail: 'user@gmail.com',
    toEmails: ['john.doe@abc.com'],
    subject: 'Lead Takip',
    body: emailBody,
    contentType: 'text/html'
  })
});
```

---

## 🔒 Güvenlik

### 1. Token Güvenliği

- **Access Token:** Kısa süreli (1-2 saat)
- **Refresh Token:** Uzun süreli (7-30 gün)
- **Token Storage:** Veritabanında şifrelenmiş
- **Token Rotation:** Otomatik yenileme

### 2. OAuth Güvenliği

- **State Parameter:** CSRF koruması
- **PKCE:** Code challenge (opsiyonel)
- **Scope Validation:** Minimum gerekli izinler
- **Redirect URI Validation:** Güvenli callback URL'leri

### 3. Email Güvenliği

- **HTTPS:** Tüm iletişim şifreli
- **API Keys:** Güvenli API anahtarları
- **Rate Limiting:** API rate limiting
- **Content Validation:** Email içerik doğrulama

### 4. Monitoring ve Logging

- **Security Logs:** OAuth işlemleri loglanır
- **Error Tracking:** Hata durumları izlenir
- **Performance Monitoring:** API performansı ölçülür
- **Audit Trail:** Tüm işlemler kaydedilir

---

## 🔧 Troubleshooting

### Yaygın Sorunlar

#### 1. "Invalid Client" Hatası
**Semptom:** OAuth callback'te "invalid_client" hatası
**Çözüm:**
- Client ID ve Client Secret'ı kontrol edin
- Redirect URI'nin doğru olduğundan emin olun
- Google Cloud Console'da OAuth consent screen'i kontrol edin

#### 2. "Invalid Scope" Hatası
**Semptom:** "invalid_scope" hatası
**Çözüm:**
- Scope'ların doğru yazıldığından emin olun
- Google Cloud Console'da gerekli API'leri etkinleştirin
- Azure Portal'da API permissions'ları kontrol edin

#### 3. "Token Expired" Hatası
**Semptom:** Email gönderirken token expired hatası
**Çözüm:**
- Token otomatik yenilenir, tekrar deneyin
- Refresh token'ın geçerli olduğundan emin olun
- Gerekirse hesabı yeniden bağlayın

#### 4. "Permission Denied" Hatası
**Semptom:** Email gönderirken permission denied
**Çözüm:**
- OAuth consent screen'de gerekli izinleri verin
- Gmail API'yi etkinleştirin
- Microsoft Graph API permissions'ları kontrol edin

### Debug Modu

```properties
# Debug logging
logging.level.com.prospect.crm.service.OAuthService=DEBUG
logging.level.com.prospect.crm.service.EmailService=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG
```

### Test Endpoint'leri

```http
# OAuth durumu test et
GET /v1/oauth/status?userId=1001

# Email durumu test et
GET /v1/emails/status?userId=1001

# Template test et
POST /v1/emails/render-template?templateName=welcome
```

---

## 📋 Best Practices

### 1. OAuth Yönetimi

- ✅ **Scope Minimization:** Sadece gerekli izinleri isteyin
- ✅ **Token Rotation:** Düzenli token yenileme
- ✅ **Error Handling:** Graceful error handling
- ✅ **Monitoring:** OAuth işlemlerini izleyin

### 2. Email Gönderimi

- ✅ **Rate Limiting:** API rate limit'lerini aşmayın
- ✅ **Content Validation:** Email içeriğini doğrulayın
- ✅ **Template Usage:** Template'leri kullanın
- ✅ **Error Handling:** Email gönderim hatalarını yakalayın

### 3. Güvenlik

- ✅ **HTTPS:** Tüm iletişimde HTTPS kullanın
- ✅ **Token Security:** Token'ları güvenli saklayın
- ✅ **Input Validation:** Tüm input'ları doğrulayın
- ✅ **Logging:** Güvenlik loglarını tutun

### 4. Performance

- ✅ **Caching:** Token'ları cache'leyin
- ✅ **Async Processing:** Email gönderimini async yapın
- ✅ **Batch Operations:** Toplu email gönderimi
- ✅ **Monitoring:** Performans metriklerini izleyin

### 5. User Experience

- ✅ **Clear Instructions:** Kullanıcılara net talimatlar verin
- ✅ **Error Messages:** Anlaşılır hata mesajları
- ✅ **Progress Indicators:** İşlem durumunu gösterin
- ✅ **Fallback Options:** SMTP fallback sağlayın

---

## 📚 Ek Kaynaklar

### OAuth2 Dokümanları
- [OAuth 2.0 RFC](https://tools.ietf.org/html/rfc6749)
- [Google OAuth2](https://developers.google.com/identity/protocols/oauth2)
- [Microsoft OAuth2](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow)

### API Dokümanları
- [Gmail API](https://developers.google.com/gmail/api)
- [Microsoft Graph API](https://docs.microsoft.com/en-us/graph/)

### Güvenlik Rehberleri
- [OAuth2 Security Best Practices](https://oauth.net/2/oauth-best-practice/)
- [Google OAuth Security](https://developers.google.com/identity/protocols/oauth2/web-security)
- [Microsoft OAuth Security](https://docs.microsoft.com/en-us/azure/active-directory/develop/security-best-practices-for-app-registration)

---

## 📞 Destek

### İletişim
- **Email:** support@prospect-crm.com
- **Documentation:** https://docs.prospect-crm.com
- **GitHub:** https://github.com/prospect-crm

### Katkıda Bulunma
1. Fork yapın
2. Feature branch oluşturun
3. Değişikliklerinizi commit edin
4. Pull request gönderin

### Lisans
Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın. 