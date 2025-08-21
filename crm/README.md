# Prospect CRM

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Prospect CRM**, müşteri ilişkileri yönetimi için geliştirilmiş kapsamlı bir Spring Boot uygulamasıdır. JWT tabanlı kimlik doğrulama, OAuth entegrasyonu, email yönetimi, abonelik sistemi ve lead yönetimi özelliklerini içerir.

## 🚀 Özellikler

- **🔐 JWT Authentication**: Access ve Refresh token sistemi
- **🔗 OAuth Integration**: Google ve Microsoft entegrasyonu
- **👥 Role-Based Access Control**: Rol ve izin tabanlı erişim kontrolü
- **📧 Email Management**: Gmail API, Microsoft Graph API ve SMTP desteği
- **💳 Subscription System**: Stripe entegrasyonu ile ödeme yönetimi
- **🎯 Lead Management**: Müşteri adayı takibi ve email tahmin sistemi
- **📝 Email Drafts**: Robot tarafından oluşturulan email taslakları
- **📊 Comprehensive Logging**: Sistem operasyonları ve hata takibi
- **📈 Daily Email Limits**: Abonelik tipine göre günlük email limitleri
- **🔄 Bounce Email Processing**: Geçersiz email tespiti ve işleme

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

## 📋 Gereksinimler

- **Java 17** veya üzeri
- **PostgreSQL 12** veya üzeri
- **Maven 3.6** veya üzeri
- **Git**

## 🚀 Kurulum

### 1. Projeyi Klonlayın

```bash
git clone https://github.com/your-username/prospect-crm.git
cd prospect-crm
```

### 2. Veritabanını Kurun

PostgreSQL veritabanınızda yeni bir veritabanı oluşturun:

```sql
CREATE DATABASE prospect_crm;
CREATE USER prospect_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE prospect_crm TO prospect_user;
```

### 3. Konfigürasyon

`src/main/resources/application.properties` dosyasını düzenleyin:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/prospect_crm
spring.datasource.username=prospect_user
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-here
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

### 4. Uygulamayı Çalıştırın

```bash
# Maven ile build
./mvnw clean package

# Uygulamayı çalıştır
java -jar target/prospect-crm-1.0.0.jar
```

Veya Maven ile doğrudan çalıştırın:

```bash
./mvnw spring-boot:run
```

Uygulama `http://localhost:8080` adresinde çalışacaktır.

## 🔧 OAuth Kurulumu

### Google OAuth2

1. [Google Cloud Console](https://console.cloud.google.com/)'a gidin
2. Yeni bir proje oluşturun
3. Google+ API'yi etkinleştirin
4. OAuth 2.0 client ID oluşturun
5. Authorized redirect URI'yi `http://localhost:8080/v1/oauth/callback` olarak ayarlayın

### Microsoft OAuth2

1. [Azure Portal](https://portal.azure.com/)'a gidin
2. Yeni bir uygulama kaydı oluşturun
3. Microsoft Graph API izinlerini ekleyin
4. Redirect URI'yi `http://localhost:8080/v1/oauth/callback` olarak ayarlayın

## 💳 Stripe Kurulumu

1. [Stripe Dashboard](https://dashboard.stripe.com/)'a gidin
2. API keys bölümünden secret key'i alın
3. Webhook endpoint'ini `http://localhost:8080/v1/payments/webhook` olarak ayarlayın
4. Webhook secret'ı alın

## 📚 API Dokümantasyonu

API dokümantasyonuna erişmek için:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- **Project Docs**: [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)

## 🧪 Test

### Unit Tests

```bash
./mvnw test
```

### Integration Tests

```bash
./mvnw verify
```

### Test Coverage

```bash
./mvnw jacoco:report
```

## 📊 Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

### System Info

```bash
curl http://localhost:8080/actuator/info
```

## 🐳 Docker

### Docker ile Çalıştırma

```bash
# Docker image oluştur
docker build -t prospect-crm .

# Container çalıştır
docker run -p 8080:8080 prospect-crm
```

### Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - postgres
  
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: prospect_crm
      POSTGRES_USER: prospect_user
      POSTGRES_PASSWORD: your_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

```bash
docker-compose up -d
```

## 🔧 Geliştirme

### Proje Yapısı

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

### Kod Stili

Proje Google Java Style Guide'ı takip eder. IntelliJ IDEA kullanıyorsanız:

1. Google Java Style Guide plugin'ini yükleyin
2. Code Style'ı GoogleStyle olarak ayarlayın
3. Import Optimize'ı etkinleştirin

### Git Workflow

```bash
# Feature branch oluştur
git checkout -b feature/new-feature

# Değişiklikleri commit et
git add .
git commit -m "feat: add new feature"

# Push et
git push origin feature/new-feature

# Pull request oluştur
```

## 🚀 Production Deployment

### Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=production
export DATABASE_URL=postgresql://host:port/database
export JWT_SECRET=your-production-secret-key
export STRIPE_SECRET_KEY=your-stripe-production-key
export OAUTH_GOOGLE_CLIENT_ID=your-google-production-client-id
export OAUTH_MICROSOFT_CLIENT_ID=your-microsoft-production-client-id
```

### Build

```bash
./mvnw clean package -Pproduction
```

### Run

```bash
java -jar -Dspring.profiles.active=production target/prospect-crm-1.0.0.jar
```

## 📝 Loglama

### Log Seviyeleri

- **INFO**: Genel bilgi mesajları
- **WARN**: Uyarı mesajları
- **ERROR**: Hata mesajları
- **SECURITY**: Güvenlik olayları

### Log Dosyaları

- `logs/application.log`: Genel uygulama logları
- `logs/error.log`: Hata logları
- `logs/security.log`: Güvenlik logları

## 🔒 Güvenlik

### JWT Security

- Access token: 2 saat geçerli
- Refresh token: 8 saat geçerli
- Güvenli cookie storage
- Otomatik token yenileme

### OAuth Security

- Secure token storage
- Automatic token refresh
- Email permissions only

### Database Security

- Prepared statements
- SQL injection protection
- Connection encryption

## 🐛 Troubleshooting

### Yaygın Sorunlar

#### Database Connection Error
```
Error: Could not create connection to database server
```
**Çözüm**: PostgreSQL servisinin çalıştığından emin olun.

#### JWT Token Error
```
Error: JWT token is invalid
```
**Çözüm**: JWT secret'ın doğru ayarlandığından emin olun.

#### OAuth Error
```
Error: OAuth authentication failed
```
**Çözüm**: OAuth client ID ve secret'ların doğru olduğundan emin olun.

#### Email Sending Error
```
Error: Failed to send email
```
**Çözüm**: SMTP ayarlarını ve OAuth token'larını kontrol edin.

### Debug Mode

Debug modunu etkinleştirmek için:

```properties
logging.level.com.prospect.crm=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📄 Lisans

Bu proje [MIT License](LICENSE) altında lisanslanmıştır.

## 📞 Destek

- **Email**: support@prospectcrm.com
- **Documentation**: [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md)
- **API Reference**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- **Issues**: [GitHub Issues](https://github.com/your-username/prospect-crm/issues)

## 🙏 Teşekkürler

- [Spring Boot](https://spring.io/projects/spring-boot) ekibine
- [PostgreSQL](https://www.postgresql.org/) ekibine
- [Stripe](https://stripe.com/) ekibine
- Tüm katkıda bulunanlara

---

**Prospect CRM** - Müşteri İlişkileri Yönetimi için Modern Çözüm

*Son Güncelleme: 2024*  
*Versiyon: 1.0.0* 