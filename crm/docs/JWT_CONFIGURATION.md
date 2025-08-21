# JWT Konfigürasyonu

## Genel Bakış

Bu döküman, Prospect CRM sistemindeki JWT (JSON Web Token) konfigürasyonunu ve güvenlik ayarlarını açıklar.

## Properties Konfigürasyonu

### application.properties

```properties
# JWT Configuration
jwt.secret=eyJhbGciOiJIUzI1NiJ9X2N1c3RvbV9zZWNyZXRfa2V5X2Zvcl9wcm9zcGVjdF9jcm1fc3lzdGVtX3ZlcnlfbG9uZ19hbmRfc2VjdXJlX2Zvcl9wcm9kdWN0aW9uX3VzZV9tYWtlX2l0X3ZlcnlfbG9uZ19hbmRfc2VjdXJl
jwt.issuer=prospect-crm
jwt.audience=prospect-crm-users
jwt.access-token-expiration=7200
jwt.refresh-token-expiration=28800
jwt.access-token-cookie-name=access_token
jwt.refresh-token-cookie-name=refresh_token
jwt.cookie-domain=localhost
jwt.cookie-path=/
jwt.cookie-secure=false
jwt.cookie-http-only=true
jwt.cookie-max-age=28800
```

## Konfigürasyon Detayları

### 1. JWT Secret
- **Değer:** 168 karakter uzunluğunda güvenli rastgele string
- **Kullanım:** Token imzalama ve doğrulama
- **Güvenlik:** Production'da environment variable olarak saklanmalı

### 2. Token Ayarları

#### Access Token
- **Süre:** 7200 saniye (2 saat)
- **Kullanım:** API istekleri için kısa süreli erişim
- **Güvenlik:** Kısa süreli, sık yenilenir

#### Refresh Token
- **Süre:** 28800 saniye (8 saat)
- **Kullanım:** Access token yenileme için
- **Güvenlik:** Uzun süreli, güvenli saklanır

### 3. Cookie Ayarları

#### Cookie İsimleri
- **Access Token:** `access_token`
- **Refresh Token:** `refresh_token`

#### Cookie Güvenlik
- **Domain:** `localhost` (development)
- **Path:** `/` (tüm path'ler)
- **Secure:** `false` (development), `true` (production)
- **HttpOnly:** `true` (XSS koruması)
- **Max Age:** 28800 saniye (8 saat)

## Kod İçerisinde Kullanım

### 1. JwtConfig Sınıfı

```java
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret;
    private String issuer;
    private String audience;
    private long accessTokenExpiration;
    private long refreshTokenExpiration;
    private String accessTokenCookieName;
    private String refreshTokenCookieName;
    private String cookieDomain;
    private String cookiePath;
    private boolean cookieSecure;
    private boolean cookieHttpOnly;
    private int cookieMaxAge;
}
```

### 2. JwtService Kullanımı

```java
@Service
public class JwtService {
    private final JwtConfig jwtConfig;
    
    // Token oluşturma
    public String generateAccessToken(Users user) {
        return generateToken(user, TokenType.ACCESS, jwtConfig.getAccessTokenExpiration());
    }
    
    // Token doğrulama
    public Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        // ...
    }
}
```

### 3. JwtAuthenticationFilter Kullanımı

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;
    
    // Cookie'den token alma
    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        // jwtConfig.getAccessTokenCookieName() kullanımı
    }
    
    // Cookie set etme
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(jwtConfig.getCookiePath());
        cookie.setDomain(jwtConfig.getCookieDomain());
        cookie.setSecure(jwtConfig.isCookieSecure());
        cookie.setHttpOnly(jwtConfig.isCookieHttpOnly());
    }
}
```

## Güvenlik Özellikleri

### 1. Token İmzalama
- **Algoritma:** HS256 (HMAC SHA-256)
- **Secret:** 168 karakter uzunluğunda güvenli string
- **Claims:** userId, email, username, tokenType

### 2. Token Doğrulama
- **İmza Kontrolü:** Her token'da imza doğrulanır
- **Süre Kontrolü:** Expiration date kontrol edilir
- **Veritabanı Kontrolü:** Token'ın revoke edilip edilmediği kontrol edilir

### 3. Cookie Güvenliği
- **HttpOnly:** JavaScript erişimi engellenir
- **Secure:** HTTPS üzerinden gönderilir (production)
- **SameSite:** CSRF koruması
- **Domain:** Sadece belirtilen domain'den erişim

## Production Ayarları

### 1. Environment Variables
```bash
# JWT Secret (production'da environment variable olarak)
export JWT_SECRET="your-super-secure-production-secret"

# Cookie ayarları
export JWT_COOKIE_SECURE=true
export JWT_COOKIE_DOMAIN=yourdomain.com
```

### 2. application-prod.properties
```properties
# Production JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.cookie-secure=true
jwt.cookie-domain=yourdomain.com
jwt.cookie-http-only=true
```

## Test Senaryoları

### 1. Token Oluşturma Testi
```bash
curl -X POST "http://localhost:8080/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

### 2. Token Doğrulama Testi
```bash
curl -X GET "http://localhost:8080/v1/users/profile" \
  -H "Cookie: access_token=your_token_here"
```

### 3. Token Yenileme Testi
```bash
curl -X POST "http://localhost:8080/v1/auth/refresh" \
  -H "Cookie: refresh_token=your_refresh_token_here"
```

## Hata Kodları

### JWT Hataları
- `JWT_EXPIRED` - Token süresi dolmuş
- `JWT_INVALID` - Geçersiz token
- `JWT_REVOKED` - Token iptal edilmiş
- `JWT_MALFORMED` - Bozuk token formatı

### HTTP Status Kodları
- `401 Unauthorized` - Token yok veya geçersiz
- `403 Forbidden` - Token var ama yetki yok
- `500 Internal Server Error` - JWT işleme hatası

## Monitoring ve Logging

### 1. JWT İşlemleri Loglanır
- Token oluşturma
- Token doğrulama
- Token yenileme
- Token iptal etme

### 2. Güvenlik Logları
- Başarısız token doğrulama
- Süresi dolmuş token'lar
- İptal edilmiş token'lar

### 3. Performance Monitoring
- Token oluşturma süresi
- Token doğrulama süresi
- Veritabanı sorgu süreleri

## Best Practices

### 1. Secret Yönetimi
- ✅ Environment variable kullan
- ❌ Kod içerisinde hardcode etme
- ✅ Düzenli secret rotation
- ❌ Version control'e commit etme

### 2. Token Yönetimi
- ✅ Kısa süreli access token
- ✅ Uzun süreli refresh token
- ✅ Token blacklisting
- ❌ Token'ları client-side'da saklama

### 3. Cookie Güvenliği
- ✅ HttpOnly flag
- ✅ Secure flag (HTTPS)
- ✅ SameSite attribute
- ❌ JavaScript erişimi

### 4. Error Handling
- ✅ Detaylı hata mesajları
- ✅ Güvenlik logları
- ✅ Rate limiting
- ❌ Hassas bilgi sızıntısı 