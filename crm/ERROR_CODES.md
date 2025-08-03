# Error Codes Documentation

## Overview
Bu dokümantasyon, Prospect CRM API'sinde kullanılan hata kodlarını açıklar. Tüm hata kodları `ERR_` prefix'i ile başlar ve kategorilere ayrılmıştır.

## Error Code Format
```
ERR_[CATEGORY][NUMBER]
```

Örnek: `ERR_2001` - User category, 001 number

## Error Code Categories

### 🔴 General Errors (ERR_1000-ERR_1999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_1000 | General error occurred | Genel sistem hatası |
| ERR_1001 | Validation error | Validasyon hatası |
| ERR_1002 | Resource not found | Kaynak bulunamadı |
| ERR_1003 | Unauthorized access | Yetkisiz erişim |
| ERR_1004 | Access forbidden | Erişim yasak |
| ERR_1005 | Bad request | Geçersiz istek |
| ERR_1006 | Internal server error | Sunucu hatası |
| ERR_1007 | Method not allowed | Metod izni yok |
| ERR_1008 | Resource conflict | Kaynak çakışması |
| ERR_1009 | Rate limit exceeded | Hız sınırı aşıldı |

### 👤 User Related Errors (ERR_2000-ERR_2999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_2000 | User not found | Kullanıcı bulunamadı |
| ERR_2001 | User already exists | Kullanıcı zaten mevcut |
| ERR_2002 | Invalid credentials | Geçersiz kimlik bilgileri |
| ERR_2003 | Username already exists | Kullanıcı adı zaten mevcut |
| ERR_2004 | Email already exists | Email zaten mevcut |
| ERR_2005 | Phone number already exists | Telefon numarası zaten mevcut |
| ERR_2006 | Password is too weak | Şifre çok zayıf |
| ERR_2007 | Account is disabled | Hesap devre dışı |
| ERR_2008 | Account is locked | Hesap kilitli |
| ERR_2009 | Token has expired | Token süresi dolmuş |
| ERR_2010 | Token is invalid | Token geçersiz |
| ERR_2011 | Token has been revoked | Token iptal edilmiş |

### 🛡️ Role Related Errors (ERR_3000-ERR_3999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_3000 | Role not found | Rol bulunamadı |
| ERR_3001 | Role already exists | Rol zaten mevcut |
| ERR_3002 | Insufficient permissions | Yetersiz yetki |
| ERR_3003 | Role is currently in use | Rol şu anda kullanımda |

### 📦 Subscription Related Errors (ERR_4000-ERR_4999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_4000 | Subscription not found | Abonelik bulunamadı |
| ERR_4001 | Subscription has expired | Abonelik süresi dolmuş |
| ERR_4002 | Subscription limit exceeded | Abonelik limiti aşıldı |
| ERR_4003 | Subscription is inactive | Abonelik pasif |
| ERR_4004 | Payment required | Ödeme gerekli |

### 🎯 Lead Related Errors (ERR_5000-ERR_5999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_5000 | Lead not found | Lead bulunamadı |
| ERR_5001 | Lead already exists | Lead zaten mevcut |
| ERR_5002 | Invalid lead data | Geçersiz lead verisi |
| ERR_5003 | Failed to guess lead email | Lead email tahmin edilemedi |

### 💳 Payment Related Errors (ERR_6000-ERR_6999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_6000 | Payment not found | Ödeme bulunamadı |
| ERR_6001 | Payment failed | Ödeme başarısız |
| ERR_6002 | Payment already processed | Ödeme zaten işlenmiş |
| ERR_6003 | Invalid payment data | Geçersiz ödeme verisi |
| ERR_6004 | Stripe session expired | Stripe oturumu süresi dolmuş |
| ERR_6005 | Invalid stripe session | Geçersiz Stripe oturumu |

### 📧 Email Related Errors (ERR_7000-ERR_7999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_7000 | Failed to send email | Email gönderilemedi |
| ERR_7001 | Email draft not found | Email taslağı bulunamadı |
| ERR_7002 | Email log not found | Email log bulunamadı |
| ERR_7003 | Invalid email format | Geçersiz email formatı |
| ERR_7004 | Email template not found | Email şablonu bulunamadı |

### 🤖 Robot Related Errors (ERR_8000-ERR_8999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_8000 | Robot not found | Robot bulunamadı |
| ERR_8001 | Robot is already running | Robot zaten çalışıyor |
| ERR_8002 | Robot execution failed | Robot çalıştırma başarısız |
| ERR_8003 | Robot execution timeout | Robot çalıştırma zaman aşımı |
| ERR_8004 | Robot log not found | Robot log bulunamadı |

### ⏱️ Rate Limit Related Errors (ERR_9000-ERR_9999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_9000 | Rate limit not found | Hız sınırı bulunamadı |
| ERR_9001 | Daily limit exceeded | Günlük limit aşıldı |
| ERR_9002 | Rate limit reset required | Hız sınırı sıfırlama gerekli |

### 🔐 OAuth Related Errors (ERR_10000-ERR_10999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_10000 | OAuth token not found | OAuth token bulunamadı |
| ERR_10001 | OAuth token expired | OAuth token süresi dolmuş |
| ERR_10002 | OAuth provider error | OAuth sağlayıcı hatası |
| ERR_10003 | OAuth authorization failed | OAuth yetkilendirme başarısız |

### ✅ Validation Specific Errors (ERR_11000-ERR_11999)
| Code | Message | Description |
|------|---------|-------------|
| ERR_11000 | Field is required | Alan zorunlu |
| ERR_11001 | Field is too short | Alan çok kısa |
| ERR_11002 | Field is too long | Alan çok uzun |
| ERR_11003 | Invalid format | Geçersiz format |
| ERR_11004 | Invalid phone number format | Geçersiz telefon numarası formatı |
| ERR_11005 | Invalid email format | Geçersiz email formatı |
| ERR_11006 | Password must contain at least one uppercase letter | Şifre en az bir büyük harf içermeli |
| ERR_11007 | Password must contain at least one lowercase letter | Şifre en az bir küçük harf içermeli |
| ERR_11008 | Password must contain at least one number | Şifre en az bir rakam içermeli |
| ERR_11009 | Password must contain at least one special character | Şifre en az bir özel karakter içermeli |

## Usage Examples

### API Response with Error
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "success": false,
  "message": "User not found",
  "data": null,
  "error": {
    "code": "ERR_2000",
    "message": "User not found",
    "details": "User with id 999 not found",
    "field": "id",
    "value": 999
  },
  "pagination": null
}
```

### Test Endpoints
- `GET /v1/test/validation-error` - ERR_2003 error
- `GET /v1/test/custom-error` - ERR_2000 error
- `GET /v1/test/error` - ERR_1006 error

## Best Practices

1. **Error Code Consistency**: Her zaman `ERR_` prefix'i kullanın
2. **Descriptive Messages**: Hata mesajları açıklayıcı olmalı
3. **Category Organization**: Hata kodları kategorilere ayrılmalı
4. **Error Details**: Hata detayları mümkün olduğunca spesifik olmalı
5. **Field Information**: Validasyon hatalarında field bilgisi ekleyin

## Adding New Error Codes

Yeni hata kodu eklerken:
1. Uygun kategoriyi seçin
2. Bir sonraki numarayı kullanın
3. Açıklayıcı mesaj ekleyin
4. Dokümantasyonu güncelleyin 