# Prospect CRM - Kapsamlı Proje Dokümantasyonu

## 📋 İçindekiler
1. [Proje Genel Bakış](#proje-genel-bakış)
2. [Teknoloji Stack](#teknoloji-stack)
3. [Mimari Yapı](#mimari-yapı)
4. [Veritabanı Tasarımı](#veritabanı-tasarımı)
5. [Güvenlik Sistemi](#güvenlik-sistemi)
6. [API Endpoints](#api-endpoints)
7. [Servis Katmanı](#servis-katmanı)
8. [Konfigürasyon](#konfigürasyon)
9. [Loglama Sistemi](#loglama-sistemi)
10. [Email Sistemi](#email-sistemi)
11. [Ödeme Sistemi](#ödeme-sistemi)
12. [OAuth Entegrasyonu](#oauth-entegrasyonu)
13. [Kullanım Kılavuzu](#kullanım-kılavuzu)
14. [Deployment](#deployment)

---

## 🎯 Proje Genel Bakış

**Prospect CRM**, müşteri ilişkileri yönetimi için geliştirilmiş kapsamlı bir Spring Boot uygulamasıdır. Sistem, lead yönetimi, email pazarlama, abonelik yönetimi ve güvenli kimlik doğrulama özelliklerini içerir.

### 🎯 Ana Özellikler
- **JWT Tabanlı Kimlik Doğrulama**: Access ve Refresh token sistemi
- **OAuth Entegrasyonu**: Gmail ve Outlook desteği
- **Role-Based Access Control (RBAC)**: Rol ve izin tabanlı erişim kontrolü
- **Email Yönetimi**: Gmail API, Microsoft Graph API ve SMTP desteği
- **Abonelik Sistemi**: Stripe entegrasyonu ile ödeme yönetimi
- **Lead Yönetimi**: Müşteri adayı takibi ve email tahmin sistemi
- **Kapsamlı Loglama**: Sistem operasyonları ve hata takibi
- **Email Draft Sistemi**: Robot tarafından oluşturulan email taslakları

---

## 🛠 Teknoloji Stack

### Backend
- **Java 17**
- **Spring Boot 3.x**
- **Spring Security 6.x**
- **Spring Data JPA**
- **PostgreSQL** (Veritabanı)
- **Maven** (Build Tool)

### Güvenlik
- **JWT (JSON Web Tokens)**
- **Spring Security**
- **OAuth2** (Google, Microsoft)

### Email & Ödeme
- **Gmail API**
- **Microsoft Graph API**
- **SMTP**
- **Stripe API**

### Loglama & Monitoring
- **SLF4J + Logback**
- **Custom System Logging**

---

## 🏗 Mimari Yapı

```
src/main/java/com/prospect/crm/
├── config/                 # Konfigürasyon sınıfları
├── constant/              # Sabitler ve enum'lar
├── controller/            # REST API Controller'ları
├── dto/                   # Data Transfer Objects
├── exception/             # Özel exception sınıfları
├── model/                 # JPA Entity'leri
├── repository/            # JPA Repository'leri
├── security/              # Güvenlik bileşenleri
├── service/               # İş mantığı servisleri
└── util/                  # Yardımcı sınıflar
```

### Katmanlı Mimari
1. **Controller Layer**: HTTP isteklerini karşılar
2. **Service Layer**: İş mantığını yönetir
3. **Repository Layer**: Veritabanı işlemlerini yapar
4. **Model Layer**: Veri yapılarını tanımlar

---

## 🗄 Veritabanı Tasarımı

### Ana Tablolar

#### Users
```sql
- id (PK)
- username
- email
- password
- name
- surname
- phone (opsiyonel)
- role (USER/ADMIN)
- is_active
- created_at
- updated_at
```

#### JwtToken
```sql
- id (PK)
- user_id (FK)
- access_token
- refresh_token
- expired
- created_at
```

#### OauthToken
```sql
- id (PK)
- user_id (FK)
- provider (GOOGLE/MICROSOFT)
- access_token
- refresh_token
- expires_at
- created_at
- updated_at
```

#### SubscriptionType
```sql
- id (PK)
- name
- code
- stripe_price_id
- duration_in_days
- description
- daily_limit
- is_active
- created_at
- updated_at
```

#### UserSubsInfo
```sql
- id (PK)
- user_id (FK)
- subscription_type_id (FK)
- subs_start_date
- subs_end_date
- is_active
- created_at
- updated_at
```

#### Lead
```sql
- id (PK)
- user_id (FK)
- company_name
- contact_name
- email
- phone
- status
- notes
- created_at
- updated_at
```

#### LeadEmailGuess
```sql
- id (PK)
- lead_id (FK)
- guessed_email
- confidence_score
- validated
- created_at
```

#### EmailDraft
```sql
- id (PK)
- user_id (FK)
- lead_id (FK)
- subject
- body
- content_type
- to_emails
- cc_emails
- bcc_emails
- attachments
- provider
- template_name
- template_data
- status (DRAFT/SENT)
- created_by_robot
- created_at
- updated_at
- sent_at
```

#### DailyEmailLimit
```sql
- id (PK)
- user_id (FK)
- date
- sent_count
- daily_limit
- created_at
```

#### BounceEmail
```sql
- id (PK)
- email_address
- bounce_type (HARD/SOFT)
- bounce_reason
- original_message_id
- provider
- processed
- created_at
```

#### SystemLog
```sql
- id (PK)
- level (INFO/WARN/ERROR/SECURITY)
- type (SYSTEM/BUSINESS/SECURITY)
- message
- details
- user_id (FK)
- ip_address
- user_agent
- timestamp
```

#### RolePermission
```sql
- id (PK)
- role_id (FK)
- permission_key
- created_at
```

---

## 🔐 Güvenlik Sistemi

### JWT Authentication
- **Access Token**: 2 saat geçerli
- **Refresh Token**: 8 saat geçerli
- **Cookie-based Storage**: Güvenli token saklama
- **Automatic Refresh**: Access token süresi dolduğunda otomatik yenileme

### Role-Based Access Control (RBAC)
- **USER**: Temel kullanıcı yetkileri
- **ADMIN**: Tam sistem yönetimi

### Permission-Based Access Control
- **@HasPermission** annotation ile method-level yetkilendirme
- **PermissionConstants** ile merkezi izin tanımları
- **PermissionEvaluator** ile dinamik izin kontrolü

### OAuth Integration
- **Google OAuth2**: Gmail API erişimi
- **Microsoft OAuth2**: Outlook API erişimi
- **Hybrid Flow**: Mevcut kullanıcı girişi / yeni kullanıcı kaydı

---

## 🌐 API Endpoints

### Authentication Endpoints
```
POST /v1/auth/login              # Kullanıcı girişi
POST /v1/auth/register           # Kullanıcı kaydı
POST /v1/auth/refresh            # Token yenileme
POST /v1/auth/logout             # Çıkış yapma
```

### OAuth Endpoints
```
POST /v1/oauth/login             # OAuth girişi
GET  /v1/oauth/callback          # OAuth callback
POST /v1/oauth/refresh           # OAuth token yenileme
```

### User Management
```
GET    /v1/users                 # Kullanıcı listesi
GET    /v1/users/{id}            # Kullanıcı detayı
PUT    /v1/users/{id}            # Kullanıcı güncelleme
DELETE /v1/users/{id}            # Kullanıcı silme
POST   /v1/users/{id}/permissions # İzin kontrolü
```

### Lead Management
```
GET    /v1/leads                 # Lead listesi
POST   /v1/leads                 # Lead oluşturma
GET    /v1/leads/{id}            # Lead detayı
PUT    /v1/leads/{id}            # Lead güncelleme
DELETE /v1/leads/{id}            # Lead silme
GET    /v1/leads/search          # Lead arama
```

### Email Management
```
POST   /v1/emails/send           # Email gönderme
GET    /v1/emails                # Email listesi
GET    /v1/emails/{id}           # Email okuma
DELETE /v1/emails/{id}           # Email silme
POST   /v1/emails/{id}/mark-read # Okundu işaretleme
```

### Email Drafts
```
GET    /v1/email-drafts          # Draft listesi
POST   /v1/email-drafts          # Draft oluşturma
GET    /v1/email-drafts/{id}     # Draft detayı
PUT    /v1/email-drafts/{id}     # Draft güncelleme
DELETE /v1/email-drafts/{id}     # Draft silme
POST   /v1/email-drafts/{id}/send # Draft gönderme
POST   /v1/email-drafts/robot    # Robot draft oluşturma
GET    /v1/email-drafts/robot    # Robot draft listesi
GET    /v1/email-drafts/all      # Tüm draft listesi
```

### Subscription Management
```
GET    /v1/subscriptions         # Abonelik listesi
POST   /v1/subscriptions         # Abonelik oluşturma
GET    /v1/subscriptions/{id}    # Abonelik detayı
PUT    /v1/subscriptions/{id}    # Abonelik güncelleme
DELETE /v1/subscriptions/{id}    # Abonelik silme
```

### Payment Integration
```
POST   /v1/payments/checkout     # Ödeme başlatma
GET    /v1/payments/success      # Başarılı ödeme
GET    /v1/payments/cancel       # İptal edilen ödeme
POST   /v1/payments/webhook      # Stripe webhook
GET    /v1/payments/portal       # Müşteri portalı
```

### System Administration
```
GET    /v1/admin/dashboard       # Admin dashboard
GET    /v1/logs                  # Sistem logları
GET    /v1/logs/cleanup          # Log temizleme
GET    /v1/system/info           # Sistem bilgileri
```

---

## 🔧 Servis Katmanı

### Core Services

#### AuthService
- Kullanıcı kimlik doğrulama
- JWT token yönetimi
- Şifre hashleme

#### JwtService
- Token oluşturma ve doğrulama
- Token yenileme
- Token iptal etme

#### UserService
- Kullanıcı CRUD işlemleri
- Rol yönetimi
- İzin kontrolü

#### LeadService
- Lead CRUD işlemleri
- Lead arama ve filtreleme
- Email tahmin sistemi

#### EmailService
- Email gönderme (Gmail/Outlook/SMTP)
- Email okuma ve listeleme
- Email silme ve işaretleme
- Bounce email işleme

#### EmailDraftService
- Email taslağı yönetimi
- Robot taslak oluşturma
- Taslak onaylama ve gönderme

#### SubscriptionService
- Abonelik durumu kontrolü
- Abonelik geçerlilik kontrolü
- Grace period yönetimi

#### StripeService
- Ödeme işlemleri
- Webhook işleme
- Müşteri portalı

#### OAuthService
- OAuth provider entegrasyonu
- Token exchange
- User info alma

#### SystemLogService
- Sistem logları
- Hata takibi
- Güvenlik logları

#### DailyEmailLimitService
- Günlük email limit kontrolü
- Limit aşımı kontrolü
- Başarılı gönderim sayacı

#### BounceEmailService
- Bounce email işleme
- Geçersiz email tespiti
- LeadEmailGuess güncelleme

---

## ⚙️ Konfigürasyon

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/prospect_crm
spring.datasource.username=postgres
spring.datasource.password=password

# JWT Configuration
jwt.secret=your-secret-key
jwt.access-token-expiration=7200
jwt.refresh-token-expiration=28800

# OAuth Configuration
oauth.google.client-id=your-google-client-id
oauth.google.client-secret=your-google-client-secret
oauth.google.redirect-uri=http://localhost:8080/v1/oauth/callback

oauth.microsoft.client-id=your-microsoft-client-id
oauth.microsoft.client-secret=your-microsoft-client-secret
oauth.microsoft.redirect-uri=http://localhost:8080/v1/oauth/callback

# Stripe Configuration
stripe.secret-key=your-stripe-secret-key
stripe.publishable-key=your-stripe-publishable-key
stripe.webhook-secret=your-webhook-secret

# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Security Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // Public endpoints
    .requestMatchers("/v1/auth/**", "/v1/oauth/**", "/v1/users/register").permitAll()
    
    // Admin only endpoints
    .requestMatchers("/v1/admin/**", "/v1/logs/**", "/v1/system/**").hasRole("ADMIN")
    
    // User endpoints
    .requestMatchers("/v1/leads/**", "/v1/emails/**", "/v1/subscriptions/**").hasRole("USER")
}
```

---

## 📝 Loglama Sistemi

### Log Seviyeleri
- **INFO**: Genel bilgi mesajları
- **WARN**: Uyarı mesajları
- **ERROR**: Hata mesajları
- **SECURITY**: Güvenlik olayları

### Log Tipleri
- **SYSTEM**: Sistem operasyonları
- **BUSINESS**: İş mantığı olayları
- **SECURITY**: Güvenlik olayları

### Log Özellikleri
- **Otomatik Temizleme**: 30 gün sonra eski loglar silinir
- **Performans İndeksleri**: Hızlı sorgu için optimize edilmiş
- **IP Adresi Takibi**: Güvenlik analizi için
- **User Agent Takibi**: Tarayıcı bilgisi

### Log Endpoints
```
GET /v1/logs                    # Log listesi
GET /v1/logs/cleanup            # Manuel temizleme
GET /v1/logs/emergency-cleanup  # Acil temizleme
GET /v1/logs/analysis           # Log analizi
```

---

## 📧 Email Sistemi

### Email Providers
1. **Gmail API**: OAuth2 ile Gmail erişimi
2. **Microsoft Graph API**: OAuth2 ile Outlook erişimi
3. **SMTP**: Fallback email gönderimi

### Email Özellikleri
- **Günlük Limit Kontrolü**: Abonelik tipine göre
- **Bounce Email İşleme**: Geçersiz email tespiti
- **Template Sistemi**: Dinamik email şablonları
- **Attachment Desteği**: Dosya ekleme
- **CC/BCC Desteği**: Çoklu alıcı

### Email Draft Sistemi
- **Robot Oluşturma**: Otomatik taslak hazırlama
- **Kullanıcı Onayı**: Taslak inceleme ve onaylama
- **OAuth Gönderimi**: Kullanıcının kendi email'inden gönderim
- **Template Entegrasyonu**: Şablon tabanlı taslaklar

### Email Endpoints
```
POST /v1/emails/send           # Email gönderme
GET  /v1/emails                # Email listesi
GET  /v1/emails/{id}           # Email okuma
DELETE /v1/emails/{id}         # Email silme
POST /v1/emails/{id}/mark-read # Okundu işaretleme
```

---

## 💳 Ödeme Sistemi

### Stripe Entegrasyonu
- **Checkout Sessions**: Güvenli ödeme sayfaları
- **Webhooks**: Gerçek zamanlı ödeme bildirimleri
- **Customer Portal**: Müşteri hesap yönetimi
- **Subscription Management**: Abonelik yönetimi

### Abonelik Tipleri
- **Basic**: 100 email/gün
- **Professional**: 500 email/gün
- **Enterprise**: 1000 email/gün

### Ödeme Akışı
1. Kullanıcı abonelik seçer
2. Stripe checkout session oluşturulur
3. Kullanıcı ödeme yapar
4. Webhook ile ödeme onaylanır
5. Abonelik aktif edilir

### Ödeme Endpoints
```
POST /v1/payments/checkout     # Ödeme başlatma
GET  /v1/payments/success      # Başarılı ödeme
GET  /v1/payments/cancel       # İptal edilen ödeme
POST /v1/payments/webhook      # Stripe webhook
GET  /v1/payments/portal       # Müşteri portalı
```

---

## 🔗 OAuth Entegrasyonu

### Desteklenen Provider'lar
- **Google**: Gmail API erişimi
- **Microsoft**: Outlook API erişimi

### OAuth Akışı
1. Kullanıcı OAuth provider seçer
2. Authorization URL'ye yönlendirilir
3. Kullanıcı izin verir
4. Callback ile token alınır
5. User info alınır
6. Mevcut kullanıcı giriş yapar / yeni kullanıcı kaydolur

### OAuth Özellikleri
- **Hybrid Flow**: Login/Register otomatik tespit
- **Token Refresh**: Otomatik token yenileme
- **Email Permissions**: Email okuma/yazma izinleri
- **Secure Storage**: Veritabanında güvenli saklama

### OAuth Endpoints
```
POST /v1/oauth/login           # OAuth girişi
GET  /v1/oauth/callback        # OAuth callback
POST /v1/oauth/refresh         # Token yenileme
```

---

## 📖 Kullanım Kılavuzu

### Kullanıcı Kaydı
1. `/v1/auth/register` endpoint'ine POST isteği
2. Gerekli bilgiler: username, email, password, name, surname
3. Sistem otomatik olarak 3 günlük trial abonelik verir

### OAuth ile Giriş
1. `/v1/oauth/login` endpoint'ine provider bilgisi ile POST
2. Kullanıcı OAuth provider'a yönlendirilir
3. İzin verildikten sonra sistem otomatik giriş yapar

### Lead Yönetimi
1. `/v1/leads` endpoint'i ile lead oluşturma
2. Sistem otomatik email tahmini yapar
3. LeadEmailGuess tablosunda tahminler saklanır

### Email Gönderimi
1. `/v1/emails/send` endpoint'i ile email gönderme
2. Sistem günlük limit kontrolü yapar
3. Başarılı gönderimler sayaca eklenir

### Email Draft Yönetimi
1. Robot otomatik taslaklar oluşturur
2. `/v1/email-drafts/robot` ile taslakları görüntüleme
3. `/v1/email-drafts/{id}/send` ile onaylama ve gönderme

### Abonelik Yönetimi
1. `/v1/subscriptions` ile mevcut abonelikleri görüntüleme
2. `/v1/payments/checkout` ile yeni abonelik satın alma
3. Stripe portal ile abonelik yönetimi

---

## 🚀 Deployment

### Gereksinimler
- **Java 17+**
- **PostgreSQL 12+**
- **Maven 3.6+**

### Build
```bash
./mvnw clean package
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/prospect-crm-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL=postgresql://host:port/database
export JWT_SECRET=your-secret-key
export STRIPE_SECRET_KEY=your-stripe-key
export OAUTH_GOOGLE_CLIENT_ID=your-google-client-id
export OAUTH_MICROSOFT_CLIENT_ID=your-microsoft-client-id
```

### Health Checks
```
GET /actuator/health     # Sistem sağlığı
GET /actuator/info       # Uygulama bilgileri
GET /actuator/metrics    # Performans metrikleri
```

---

## 🔧 Bakım ve İzleme

### Log Monitoring
- Sistem logları düzenli kontrol edilmeli
- Hata logları anında incelenmeli
- Güvenlik logları takip edilmeli

### Database Maintenance
- Düzenli backup alınmalı
- Performans indeksleri kontrol edilmeli
- Eski loglar temizlenmeli

### Email System
- Bounce email'ler düzenli işlenmeli
- Email limitleri kontrol edilmeli
- OAuth token'ları yenilenmeli

### Security Updates
- JWT secret'ları düzenli değiştirilmeli
- OAuth client secret'ları güncellenmeli
- Stripe webhook secret'ları kontrol edilmeli

---

## 📞 Destek ve İletişim

### Teknik Destek
- **Email**: support@prospectcrm.com
- **Documentation**: https://docs.prospectcrm.com
- **API Reference**: https://api.prospectcrm.com/docs

### Geliştirme Ekibi
- **Lead Developer**: [Developer Name]
- **Backend Team**: [Team Email]
- **DevOps**: [DevOps Email]

---

## 📄 Lisans

Bu proje [MIT License](LICENSE) altında lisanslanmıştır.

---

*Son Güncelleme: 2024*
*Versiyon: 1.0.0* 