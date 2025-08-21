# Abonelik Yönetimi Sistemi

## Genel Bakış

Bu sistem, kullanıcıların abonelik durumlarını kontrol eder ve farklı aşamalarda farklı erişim seviyeleri sağlar. Sistem, abonelik süresi dolan kullanıcılara 3 günlük ek süre (grace period) tanır ve deneme süresi biten kullanıcıları sadece abonelik alma endpoint'lerine yönlendirir.

## Abonelik Durumları

### 1. ACTIVE (Aktif Abonelik)
- **Durum:** Kullanıcının aktif bir aboneliği var
- **Erişim:** Tüm endpoint'lere erişim
- **Mesaj:** "Aktif abonelik"

### 2. GRACE_PERIOD (Ek Süre Dönemi)
- **Durum:** Abonelik süresi dolmuş, 3 günlük ek süre verilmiş
- **Erişim:** Tüm endpoint'lere erişim (uyarı ile)
- **Mesaj:** "Abonelik süresi dolmuş. X gün ek süreniz kaldı."

### 3. EXPIRED (Süresi Dolmuş)
- **Durum:** Normal abonelik süresi ve grace period bitmiş
- **Erişim:** Sadece abonelik alma endpoint'leri
- **Mesaj:** "Abonelik süreniz dolmuş. Yenileme yapın."

### 4. TRIAL_EXPIRED (Deneme Süresi Dolmuş)
- **Durum:** Deneme aboneliği süresi dolmuş
- **Erişim:** Sadece abonelik alma endpoint'leri
- **Mesaj:** "Deneme süreniz dolmuş. Abonelik satın alın."

### 5. NO_SUBSCRIPTION (Abonelik Yok)
- **Durum:** Hiç abonelik yok
- **Erişim:** Sadece abonelik alma endpoint'leri
- **Mesaj:** "Abonelik bulunamadı"

## Sistem Bileşenleri

### 1. SubscriptionService
Abonelik durumu kontrolü ve yönetimi için ana servis.

#### Ana Metodlar:
- `checkSubscriptionStatus(Long userId)` - Kullanıcının abonelik durumunu kontrol eder
- `canAccessEndpoint(Long userId, String endpoint)` - Belirli endpoint'e erişim izni kontrol eder
- `getUsersInGracePeriod()` - Grace period'da olan kullanıcıları getirir
- `getUsersWithExpiredGracePeriod()` - Grace period'ı biten kullanıcıları getirir
- `extendSubscription(Long userId, int days)` - Abonelik süresini uzatır
- `cancelSubscription(Long userId)` - Aboneliği iptal eder

### 2. JwtAuthenticationFilter
JWT token kontrolü sırasında abonelik kontrolü yapar.

#### Kontrol Edilen Endpoint'ler:
- **Public Endpoint'ler:** Admin, auth, users, payment callbacks (abonelik kontrolü yapılmaz)
- **Korumalı Endpoint'ler:** Leads, subscriptions, diğer business endpoint'ler abonelik kontrolüne tabi

### 3. SubscriptionController
Abonelik yönetimi için REST API endpoint'leri.

## API Endpoint'leri

### Kullanıcı Endpoint'leri

#### 1. Abonelik Durumu Kontrolü
```http
GET /v1/subscriptions/status?userId=1002
```

**Response:**
```json
{
  "success": true,
  "data": {
    "hasSubscription": true,
    "status": "ACTIVE",
    "message": "Aktif abonelik",
    "daysRemaining": 25,
    "canAccess": true,
    "allowedEndpoints": ["*"],
    "subscriptionType": "Premium Plan",
    "startDate": "2024-01-01T10:00:00",
    "endDate": "2024-02-01T10:00:00",
    "gracePeriodEnd": "2024-02-04T10:00:00"
  }
}
```

#### 2. Abonelik Planı Değiştirme
```http
POST /v1/subscriptions/change-plan?userId=1002&newPlanCode=premium
```

### Admin Endpoint'leri

#### 1. Grace Period Kullanıcıları
```http
GET /v1/subscriptions/grace-period
```

#### 2. Grace Period'ı Biten Kullanıcılar
```http
GET /v1/subscriptions/expired-grace-period
```

#### 3. Abonelik Uzatma
```http
POST /v1/subscriptions/extend?userId=1002&days=30
```

#### 4. Abonelik İptal
```http
POST /v1/subscriptions/cancel?userId=1002
```

#### 5. Abonelik Dashboard
```http
GET /v1/admin/subscription-dashboard
```

## Erişim Kontrolü

### Aktif Abonelik
- ✅ Tüm endpoint'lere erişim
- ✅ Tam sistem kullanımı

### Grace Period (3 Gün)
- ✅ Tüm endpoint'lere erişim
- ⚠️ Uyarı mesajı ile
- 📧 Otomatik hatırlatma e-postaları

### Süresi Dolmuş Abonelik
- ❌ Sadece abonelik alma endpoint'leri
- ✅ `/v1/payments/create-checkout-session`
- ✅ `/v1/payments/subscription-types`
- ✅ `/v1/auth/**`
- ✅ `/v1/users/**`

## Konfigürasyon

### Grace Period Süresi
```java
private static final int GRACE_PERIOD_DAYS = 3;
```

### Abonelik Tipleri
- **Trial:** 3 gün, $0.00
- **Basic:** 30 gün, $9.99
- **Premium:** 30 gün, $19.99
- **Enterprise:** 30 gün, $49.99

## Hata Kodları

### Abonelik Durumu Hataları
- `NO_SUBSCRIPTION` - Abonelik bulunamadı
- `SUBSCRIPTION_EXPIRED` - Abonelik süresi dolmuş
- `TRIAL_EXPIRED` - Deneme süresi dolmuş
- `GRACE_PERIOD` - Ek süre dönemi

### HTTP Status Kodları
- `402 Payment Required` - Abonelik gerekli
- `403 Forbidden` - Erişim reddedildi

## Test Senaryoları

### 1. Aktif Abonelik Testi
```bash
curl -X GET "http://localhost:8080/v1/subscriptions/status?userId=1001" \
  -H "Cookie: access_token=..."
```

### 2. Grace Period Testi
```bash
# Abonelik süresi dolmuş kullanıcı için
curl -X GET "http://localhost:8080/v1/leads" \
  -H "X-User-ID: 1002"
```

### 3. Süresi Dolmuş Abonelik Testi
```bash
# Grace period'ı da bitmiş kullanıcı için
curl -X GET "http://localhost:8080/v1/leads" \
  -H "X-User-ID: 1003"
```

## Admin Dashboard

### Abonelik Dashboard'u
Admin panelinde abonelik durumlarını görüntülemek için:

```http
GET /v1/admin/subscription-dashboard
```

**Response:**
```json
{
  "success": true,
  "data": {
    "gracePeriodUsers": 5,
    "gracePeriodUsersList": [...],
    "expiredGracePeriodUsers": 2,
    "expiredGracePeriodUsersList": [...]
  }
}
```

## Otomatik İşlemler

### 1. JWT Token + Abonelik Kontrolü
- Her istekte JWT token kontrolü
- Korumalı endpoint'ler için abonelik kontrolü
- 3 günlük grace period hesaplama
- Durum bazlı erişim kontrolü

### 2. Abonelik Yenileme
- Stripe webhook ile otomatik yenileme
- Başarısız ödemeler için grace period
- Otomatik abonelik iptali

## Güvenlik

### 1. JWT Token Kontrolü
- Her istekte JWT token doğrulama
- User ID extraction
- Token expiration kontrolü

### 2. Role-Based Access
- Admin endpoint'leri sadece ADMIN rolü
- User endpoint'leri USER rolü gerektirir
- Public endpoint'ler herkese açık

### 3. Endpoint Güvenliği
- JwtAuthenticationFilter ile JWT + abonelik kontrolü
- 402 Payment Required response
- Detaylı hata mesajları

## Monitoring ve Logging

### 1. Abonelik Durumu Logları
- Her abonelik kontrolü loglanır
- Grace period geçişleri
- Erişim reddedilen istekler

### 2. Admin Dashboard
- Grace period kullanıcı sayısı
- Süresi dolmuş abonelik sayısı
- Sistem performans metrikleri

### 3. Alert Sistemi
- Grace period kullanıcıları için uyarı
- Süresi dolmuş abonelikler için bildirim
- Admin dashboard'da görsel uyarılar

## Gelecek Geliştirmeler

### 1. Otomatik E-posta Bildirimleri
- Grace period başlangıcında uyarı
- Süresi dolmadan önce hatırlatma
- Abonelik yenileme teşvikleri

### 2. Abonelik Yükseltme/Düşürme
- Stripe ile entegre plan değişikliği
- Pro-rata hesaplamalar
- Otomatik fiyatlandırma

### 3. Analytics ve Raporlama
- Abonelik dönüşüm oranları
- Churn analizi
- Gelir raporları

### 4. A/B Testing
- Farklı grace period süreleri
- Farklı abonelik planları
- Fiyatlandırma testleri

## API Endpoint'leri

### Public Endpoint'ler (Abonelik Kontrolü Yok):
```http
GET /v1/admin/**
GET /v1/auth/**
GET /v1/users/**
GET /v1/payments/success
GET /v1/payments/cancel
POST /v1/payments/webhook
GET /v1/health/**
GET /v1/test/**
```

### Korumalı Endpoint'ler (Abonelik Kontrolü Var):
```http
GET /v1/leads/**
GET /v1/subscriptions/**
POST /v1/payments/create-checkout-session
GET /v1/payments/subscription-types
``` 