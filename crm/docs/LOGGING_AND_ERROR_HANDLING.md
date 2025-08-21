# Loglama ve Hata Yönetimi Dokümantasyonu

## 📋 İçindekiler
1. [Genel Bakış](#genel-bakış)
2. [Loglama Sistemi](#loglama-sistemi)
3. [Error Code Sistemi](#error-code-sistemi)
4. [Kullanım Örnekleri](#kullanım-örnekleri)
5. [API Endpoint'leri](#api-endpointleri)
6. [Performans Optimizasyonları](#performans-optimizasyonları)
7. [Otomatik Temizlik](#otomatik-temizlik)
8. [Best Practices](#best-practices)

---

## 🎯 Genel Bakış

Bu dokümantasyon, Prospect CRM sistemindeki loglama ve hata yönetimi yapısını detaylandırır. Sistem, kapsamlı bir loglama altyapısı ve standartlaştırılmış hata kodları ile güvenli ve izlenebilir bir ortam sağlar.

---

## 📊 Loglama Sistemi

### 🏗️ Sistem Mimarisi

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │───▶│  SystemLogService│───▶│  SystemLogRepository │
│   (Controllers) │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ GlobalException │    │  LogCleanup     │    │   Database      │
│    Handler      │    │  Scheduler      │    │   (system_logs) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 📝 Log Seviyeleri (LogLevel)

| Seviye | Açıklama | Kullanım Alanı |
|--------|----------|----------------|
| `DEBUG` | Detaylı debug bilgileri | Geliştirme aşamasında |
| `INFO` | Genel bilgi mesajları | Normal işlem akışı |
| `WARN` | Uyarı mesajları | Potansiyel sorunlar |
| `ERROR` | Hata mesajları | İşlem başarısız |
| `FATAL` | Kritik hatalar | Sistem çökmesi |

### 🏷️ Log Tipleri (LogType)

| Tip | Açıklama | Kullanım Alanı |
|-----|----------|----------------|
| `SYSTEM` | Sistem logları | Genel sistem olayları |
| `SECURITY` | Güvenlik logları | Kimlik doğrulama, yetkilendirme |
| `PERFORMANCE` | Performans logları | Yavaş sorgular, performans metrikleri |
| `BUSINESS` | İş logları | İş süreçleri, kullanıcı aktiviteleri |
| `AUDIT` | Denetim logları | Değişiklik izleme, compliance |
| `ERROR` | Hata logları | Exception'lar, hatalar |
| `API` | API logları | HTTP istekleri, response'lar |
| `DATABASE` | Veritabanı logları | SQL sorguları, bağlantılar |
| `EXTERNAL_SERVICE` | Dış servis logları | Third-party entegrasyonlar |

### 🗄️ SystemLog Entity

```java
@Entity
@Table(name = "system_logs", indexes = {
    @Index(name = "idx_system_logs_timestamp", columnList = "timestamp"),
    @Index(name = "idx_system_logs_level", columnList = "level"),
    @Index(name = "idx_system_logs_type", columnList = "type"),
    @Index(name = "idx_system_logs_user_id", columnList = "userId"),
    @Index(name = "idx_system_logs_ip_address", columnList = "ipAddress"),
    @Index(name = "idx_system_logs_level_timestamp", columnList = "level, timestamp"),
    @Index(name = "idx_system_logs_type_timestamp", columnList = "type, timestamp"),
    @Index(name = "idx_system_logs_user_timestamp", columnList = "userId, timestamp"),
    @Index(name = "idx_system_logs_http_status", columnList = "httpStatus"),
    @Index(name = "idx_system_logs_endpoint", columnList = "endpoint"),
    @Index(name = "idx_system_logs_execution_time", columnList = "executionTime")
})
public class SystemLog {
    private Long id;
    private LogLevel level;
    private LogType type;
    private String message;
    private String details;
    private String stackTrace;
    private String className;
    private String methodName;
    private String userId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private Long executionTime;
    private String requestId;
    private String endpoint;
    private String httpMethod;
    private Integer httpStatus;
    private String requestBody;
    private String responseBody;
}
```

---

## 🚨 Error Code Sistemi

### 📋 Error Code Kategorileri

| Kategori | Aralık | Açıklama |
|----------|--------|----------|
| **General Errors** | ERR_1000-ERR_1999 | Genel sistem hataları |
| **User Related** | ERR_2000-ERR_2999 | Kullanıcı işlemleri |
| **Role Related** | ERR_3000-ERR_3999 | Rol ve yetki hataları |
| **Subscription** | ERR_4000-ERR_4999 | Abonelik işlemleri |
| **User Subscription** | ERR_4100-ERR_4199 | Kullanıcı abonelik bilgileri |
| **Lead Related** | ERR_5000-ERR_5999 | Lead işlemleri |
| **Payment** | ERR_6000-ERR_6999 | Ödeme işlemleri |
| **Email** | ERR_7000-ERR_7999 | Email işlemleri |
| **Robot** | ERR_8000-ERR_8999 | Robot işlemleri |
| **Rate Limit** | ERR_9000-ERR_9999 | Rate limiting |
| **JWT Token** | ERR_9500-ERR_9599 | JWT token işlemleri |
| **OAuth** | ERR_10000-ERR_10999 | OAuth işlemleri |
| **Validation** | ERR_11000-ERR_11999 | Validasyon hataları |
| **System Log** | ERR_12000-ERR_12999 | Loglama hataları |

### 🔧 Önemli Error Code'lar

#### Genel Hatalar
```java
GENERAL_ERROR("ERR_1000", "General error occurred")
VALIDATION_ERROR("ERR_1001", "Validation error")
RESOURCE_NOT_FOUND("ERR_1002", "Resource not found")
UNAUTHORIZED("ERR_1003", "Unauthorized access")
FORBIDDEN("ERR_1004", "Access forbidden")
BAD_REQUEST("ERR_1005", "Bad request")
INTERNAL_SERVER_ERROR("ERR_1006", "Internal server error")
```

#### Kullanıcı Hataları
```java
USER_NOT_FOUND("ERR_2000", "User not found")
USER_ALREADY_EXISTS("ERR_2001", "User already exists")
INVALID_CREDENTIALS("ERR_2002", "Invalid credentials")
USERNAME_ALREADY_EXISTS("ERR_2003", "Username already exists")
EMAIL_ALREADY_EXISTS("ERR_2004", "Email already exists")
```

#### Loglama Hataları
```java
SYSTEM_LOG_NOT_FOUND("ERR_12000", "System log not found")
LOG_CREATION_FAILED("ERR_12004", "Failed to create log entry")
LOG_QUERY_FAILED("ERR_12005", "Failed to query logs")
LOG_CLEANUP_FAILED("ERR_12006", "Failed to cleanup old logs")
MANUAL_CLEANUP_FAILED("ERR_12007", "Manual cleanup failed")
EMERGENCY_CLEANUP_FAILED("ERR_12008", "Emergency cleanup failed")
```

---

## 💻 Kullanım Örnekleri

### 🔧 Service Katmanında Loglama

```java
@Service
public class UserService {
    
    private final SystemLogService systemLogService;
    
    public User createUser(UserRequestDto userRequestDto) {
        try {
            // İş mantığı
            User user = userRepository.save(newUser);
            
            // Başarılı işlem logu
            systemLogService.logBusiness(
                "User created successfully",
                "User ID: " + user.getId() + ", Email: " + user.getEmail(),
                user.getId().toString()
            );
            
            return user;
            
        } catch (Exception e) {
            // Hata logu
            systemLogService.logError(
                "Failed to create user",
                "User creation failed for email: " + userRequestDto.getEmail(),
                e.getStackTrace().toString(),
                "UserService",
                "createUser"
            );
            throw e;
        }
    }
}
```

### 🛡️ Exception Handling

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private final SystemLogService systemLogService;
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        // Uyarı logu
        logWarn("Resource Not Found Exception", ex, request);
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        "Resource not found",
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ex.getMessage()
                ));
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        // Güvenlik logu
        logSecurity("Authentication Exception", ex, request);
        
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "Authentication failed",
                        ErrorCode.INVALID_CREDENTIALS.getCode(),
                        ex.getMessage()
                ));
    }
}
```

### 📊 API Loglama

```java
@RestController
public class UserController {
    
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserRequestDto userRequestDto) {
        long startTime = System.currentTimeMillis();
        
        try {
            User user = userService.createUser(userRequestDto);
            
            // API başarı logu
            systemLogService.logApi(
                "User creation API called successfully",
                "/api/v1/users",
                "POST",
                201,
                userRequestDto.toString(),
                user.toString(),
                System.currentTimeMillis() - startTime,
                getCurrentUserId(),
                getClientIpAddress()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(user, "User created successfully"));
                    
        } catch (Exception e) {
            // API hata logu
            systemLogService.logApi(
                "User creation API failed",
                "/api/v1/users",
                "POST",
                500,
                userRequestDto.toString(),
                e.getMessage(),
                System.currentTimeMillis() - startTime,
                getCurrentUserId(),
                getClientIpAddress()
            );
            throw e;
        }
    }
}
```

---

## 🌐 API Endpoint'leri

### 📋 Log Sorgulama Endpoint'leri

#### Tüm Logları Getir
```http
GET /v1/logs
```

#### ID ile Log Getir
```http
GET /v1/logs/{id}
```

#### Seviyeye Göre Loglar
```http
GET /v1/logs/level/{level}
```
**Seviyeler:** `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`

#### Tipe Göre Loglar
```http
GET /v1/logs/type/{type}
```
**Tipler:** `SYSTEM`, `SECURITY`, `PERFORMANCE`, `BUSINESS`, `AUDIT`, `ERROR`, `API`, `DATABASE`, `EXTERNAL_SERVICE`

#### Hata Logları
```http
GET /v1/logs/errors
```

#### Tarih Aralığına Göre
```http
GET /v1/logs/date-range?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
```

#### Kullanıcı Logları
```http
GET /v1/logs/user/{userId}
```

#### Arama
```http
GET /v1/logs/search?keyword=error
```

#### Yavaş Sorgular
```http
GET /v1/logs/slow-queries?threshold=1000
```

### 🗑️ Log Temizleme Endpoint'leri

#### Otomatik Temizlik
```http
POST /v1/logs/cleanup
```

#### Temizlik Öncesi Kontrol
```http
GET /v1/logs/cleanup/count
```

#### Manuel Temizlik
```http
POST /v1/logs/cleanup/manual?days=30&level=INFO
POST /v1/logs/cleanup/manual?days=7&type=API
```

#### Acil Durum Temizliği
```http
POST /v1/logs/cleanup/emergency?days=7
```

### 📊 İstatistik Endpoint'leri

#### Seviyeye Göre Sayım
```http
GET /v1/logs/stats/level-count?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
```

#### Tipe Göre Sayım
```http
GET /v1/logs/stats/type-count?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59
```

#### Hata Sayısı
```http
GET /v1/logs/stats/error-count?startDate=2024-01-01T00:00:00
```

### 📄 Sayfalama Endpoint'leri

#### Genel Sayfalama
```http
GET /v1/logs/page?page=0&size=20
```

#### Seviyeye Göre Sayfalama
```http
GET /v1/logs/page/level/INFO?page=0&size=20
```

#### Tipe Göre Sayfalama
```http
GET /v1/logs/page/type/API?page=0&size=20
```

---

## ⚡ Performans Optimizasyonları

### 🗂️ Database Index'leri

```sql
-- Temel index'ler
CREATE INDEX idx_system_logs_timestamp ON system_logs(timestamp);
CREATE INDEX idx_system_logs_level ON system_logs(level);
CREATE INDEX idx_system_logs_type ON system_logs(type);
CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);
CREATE INDEX idx_system_logs_ip_address ON system_logs(ip_address);

-- Composite index'ler (performans için)
CREATE INDEX idx_system_logs_level_timestamp ON system_logs(level, timestamp);
CREATE INDEX idx_system_logs_type_timestamp ON system_logs(type, timestamp);
CREATE INDEX idx_system_logs_user_timestamp ON system_logs(user_id, timestamp);

-- API ve performans index'leri
CREATE INDEX idx_system_logs_http_status ON system_logs(http_status);
CREATE INDEX idx_system_logs_endpoint ON system_logs(endpoint);
CREATE INDEX idx_system_logs_execution_time ON system_logs(execution_time);
```

### 📈 Performans Faydaları

- **Hızlı sorgular**: Index'ler sayesinde
- **Azalan disk kullanımı**: Eski loglar temizlenir
- **Optimize edilmiş tablo boyutu**: Sürekli büyüme engellenir
- **Hızlı log yazma**: Optimize edilmiş index'ler
- **Hızlı log okuma**: Composite index'ler

---

## 🗑️ Otomatik Temizlik

### ⏰ Zamanlanmış Görevler

#### Günlük Temizlik (02:00)
```java
@Scheduled(cron = "0 0 2 * * ?")
public void cleanupOldLogs() {
    // 1 ay önceki hata olmayan logları siler
    // ERROR ve FATAL logları korunur
}
```

#### Haftalık Analiz (Pazar 03:00)
```java
@Scheduled(cron = "0 0 3 ? * SUN")
public void weeklyLogAnalysis() {
    // Haftalık log istatistikleri
    // Toplam log ve hata log sayıları
}
```

#### Aylık Rapor (Ayın 1'i 04:00)
```java
@Scheduled(cron = "0 0 4 1 * ?")
public void monthlyLogReport() {
    // Aylık detaylı log raporu
    // Toplam, hata ve uyarı log sayıları
}
```

### 🛡️ Temizlik Stratejisi

#### Korunan Log Türleri
- ✅ **ERROR** logları korunur
- ✅ **FATAL** logları korunur
- ❌ **INFO, WARN, DEBUG** logları silinir

#### Temizlik Seçenekleri
- **Varsayılan**: 1 ay önceki hata olmayan loglar
- **Manuel**: Belirli gün/level/type seçimi
- **Acil**: Tüm logları silme (dikkatli kullanım)

---

## 📋 Best Practices

### 🔧 Loglama Best Practices

#### 1. Doğru Log Seviyesi Kullanımı
```java
// ✅ Doğru
systemLogService.logInfo("User login successful", "User logged in", "AuthService", "login");
systemLogService.logWarn("Rate limit approaching", "User approaching rate limit", "RateLimitService", "checkLimit");
systemLogService.logError("Database connection failed", "Connection timeout", stackTrace, "DatabaseService", "connect");

// ❌ Yanlış
systemLogService.logError("User login successful", "User logged in", "AuthService", "login");
```

#### 2. Anlamlı Mesajlar
```java
// ✅ Doğru
systemLogService.logBusiness("Payment processed", "Amount: $100, User: john@example.com", userId);

// ❌ Yanlış
systemLogService.logBusiness("OK", "Success", userId);
```

#### 3. Güvenlik Bilgilerini Loglama
```java
// ✅ Doğru - IP adresi ve user agent loglanır
systemLogService.logSecurity("Failed login attempt", "Invalid credentials", userId, ipAddress, userAgent);

// ❌ Yanlış - Hassas bilgiler loglanmaz
systemLogService.logSecurity("Password: 123456", "User credentials", userId, ipAddress, userAgent);
```

### 🚨 Error Handling Best Practices

#### 1. Uygun Error Code Kullanımı
```java
// ✅ Doğru
throw new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND.getMessage() + ": " + userId);

// ❌ Yanlış
throw new RuntimeException("User not found");
```

#### 2. Exception Chaining
```java
// ✅ Doğru
try {
    userService.createUser(userRequestDto);
} catch (ValidationException e) {
    systemLogService.logError("User creation failed", e.getMessage(), e.getStackTrace().toString(), 
                             "UserController", "createUser");
    throw new BadRequestException(ErrorCode.VALIDATION_ERROR.getMessage(), e);
}
```

#### 3. Global Exception Handling
```java
// ✅ Doğru - GlobalExceptionHandler'da tüm exception'lar yakalanır
@ExceptionHandler(Exception.class)
public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
    logError("Global Exception", ex, request);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("An unexpected error occurred", 
                                   ErrorCode.INTERNAL_SERVER_ERROR.getCode(), 
                                   ex.getMessage()));
}
```

### 📊 Monitoring Best Practices

#### 1. Performans Metrikleri
```java
// ✅ Doğru - Execution time loglanır
long startTime = System.currentTimeMillis();
// ... işlem ...
systemLogService.logPerformance("Database query completed", 
                               System.currentTimeMillis() - startTime, 
                               "UserRepository", "findByEmail");
```

#### 2. Business Metrics
```java
// ✅ Doğru - İş metrikleri loglanır
systemLogService.logBusiness("Order placed", 
                            "Order ID: " + orderId + ", Total: $" + totalAmount, 
                            userId);
```

#### 3. Security Monitoring
```java
// ✅ Doğru - Güvenlik olayları loglanır
systemLogService.logSecurity("Suspicious activity detected", 
                            "Multiple failed login attempts from IP: " + ipAddress, 
                            userId, ipAddress, userAgent);
```

---

## 🔍 Troubleshooting

### Yaygın Sorunlar ve Çözümleri

#### 1. Log Tablosu Çok Büyük
**Sorun:** `system_logs` tablosu çok büyük, sorgular yavaş
**Çözüm:** 
```http
POST /v1/logs/cleanup/emergency?days=7
```

#### 2. Çok Fazla Hata Logu
**Sorun:** Sürekli aynı hata loglanıyor
**Çözüm:** Log seviyesini kontrol edin ve gereksiz logları azaltın

#### 3. Performans Sorunları
**Sorun:** Log sorguları yavaş
**Çözüm:** Index'lerin doğru oluşturulduğunu kontrol edin

#### 4. Disk Alanı Sorunu
**Sorun:** Disk alanı doluyor
**Çözüm:** Otomatik temizlik çalışıyor mu kontrol edin

---

## 📞 Destek

Bu dokümantasyon ile ilgili sorularınız için:
- **Email:** support@prospectcrm.com
- **Dokümantasyon:** [docs.prospectcrm.com](https://docs.prospectcrm.com)
- **GitHub Issues:** [github.com/prospectcrm/issues](https://github.com/prospectcrm/issues)

---

*Son güncelleme: 2024-01-XX*
*Versiyon: 1.0.0* 