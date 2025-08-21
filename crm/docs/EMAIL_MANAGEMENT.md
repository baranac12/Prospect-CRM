# Email Yönetimi - Gelişmiş Özellikler

## 📋 İçindekiler

1. [Genel Bakış](#genel-bakış)
2. [OAuth Scope'ları](#oauth-scopeları)
3. [Email İşlemleri](#email-işlemleri)
4. [API Endpoint'leri](#api-endpointleri)
5. [Kullanım Örnekleri](#kullanım-örnekleri)
6. [Güvenlik](#güvenlik)
7. [Troubleshooting](#troubleshooting)

---

## 🔐 Genel Bakış

Email yönetimi artık Gmail ve Outlook için kapsamlı işlemleri destekliyor:

### ✅ Desteklenen İşlemler

- **Email Gönderme:** Gmail API ve Microsoft Graph API ile
- **Email Okuma:** Tam email içeriği ve eklerle
- **Email Listesi:** Filtreleme, arama ve sayfalama ile
- **Email Silme:** Tekil veya toplu silme
- **Email Etiketleme:** Okundu/okunmadı, yıldızlı işaretleme
- **SMTP Fallback:** OAuth başarısız olursa SMTP ile gönderim

### 🔧 Teknik Özellikler

- **OAuth2 Entegrasyonu:** Güvenli token yönetimi
- **API Rate Limiting:** Provider limitlerine uyum
- **Error Handling:** Kapsamlı hata yönetimi
- **Logging:** Tüm işlemler loglanır
- **Template Support:** Hazır email template'leri

---

## 🔑 OAuth Scope'ları

### Gmail API Scope'ları

```properties
# Gmail OAuth2 Scopes
openid
profile
email
https://www.googleapis.com/auth/gmail.send          # Email gönderme
https://www.googleapis.com/auth/gmail.readonly      # Email okuma
https://www.googleapis.com/auth/gmail.modify        # Email düzenleme (etiketleme)
https://www.googleapis.com/auth/gmail.labels        # Etiket yönetimi
```

### Microsoft Graph API Scope'ları

```properties
# Microsoft OAuth2 Scopes
openid
profile
email
offline_access
https://graph.microsoft.com/Mail.Send               # Email gönderme
https://graph.microsoft.com/Mail.Read               # Email okuma
https://graph.microsoft.com/Mail.ReadWrite          # Email okuma/yazma
https://graph.microsoft.com/Mail.ReadWrite.Shared   # Paylaşılan mailbox erişimi
```

---

## 📧 Email İşlemleri

### 1. Email Gönderme

```java
// Gmail veya Outlook ile email gönderme
EmailSendRequestDto request = EmailSendRequestDto.builder()
    .provider("google")                    // google, microsoft
    .fromEmail("user@gmail.com")
    .toEmails(List.of("recipient@example.com"))
    .ccEmails(List.of("cc@example.com"))   // Opsiyonel
    .bccEmails(List.of("bcc@example.com")) // Opsiyonel
    .subject("Test Email")
    .body("<h1>Merhaba!</h1><p>Bu bir test emailidir.</p>")
    .contentType("text/html")              // text/plain, text/html
    .attachments(List.of(attachment))      // Opsiyonel
    .build();
```

### 2. Email Okuma

```java
// Email okuma
EmailReadRequestDto request = EmailReadRequestDto.builder()
    .provider("google")
    .emailId("email_id_here")
    .format("full")                        // full, minimal, raw
    .includeAttachments(true)              // Ekleri dahil et
    .build();
```

### 3. Email Listesi

```java
// Email listesi alma
EmailListRequestDto request = EmailListRequestDto.builder()
    .provider("google")
    .label("INBOX")                        // INBOX, SENT, DRAFT, SPAM, TRASH
    .query("from:example.com")             // Arama sorgusu
    .maxResults(50)                        // Maksimum sonuç
    .pageToken("next_page_token")          // Sayfalama
    .includeSpamTrash(false)               // Spam/çöp kutusunu dahil et
    .orderBy("internalDate")               // Sıralama
    .sortOrder("descending")               // ascending, descending
    .build();
```

### 4. Email Silme

```java
// Email silme
EmailDeleteRequestDto request = EmailDeleteRequestDto.builder()
    .provider("google")
    .emailIds(List.of("email_id_1", "email_id_2"))
    .permanentDelete(false)                // true: kalıcı sil, false: çöp kutusuna taşı
    .build();
```

### 5. Email Etiketleme

```java
// Email'leri okundu olarak işaretle
emailService.markAsRead(userId, "google", List.of("email_id_1", "email_id_2"));

// Email'leri okunmadı olarak işaretle
emailService.markAsUnread(userId, "google", List.of("email_id_1", "email_id_2"));

// Email'leri yıldızlı yap
emailService.starEmails(userId, "google", List.of("email_id_1", "email_id_2"));

// Email'lerden yıldızı kaldır
emailService.unstarEmails(userId, "google", List.of("email_id_1", "email_id_2"));
```

---

## 🌐 API Endpoint'leri

### Email Gönderme

```http
POST /v1/emails/send?userId=123
Content-Type: application/json

{
  "provider": "google",
  "fromEmail": "user@gmail.com",
  "toEmails": ["recipient@example.com"],
  "ccEmails": ["cc@example.com"],
  "bccEmails": ["bcc@example.com"],
  "subject": "Test Email",
  "body": "<h1>Merhaba!</h1><p>Bu bir test emailidir.</p>",
  "contentType": "text/html",
  "attachments": [
    {
      "fileName": "document.pdf",
      "contentType": "application/pdf",
      "base64Content": "base64_encoded_content"
    }
  ]
}
```

### Email Okuma

```http
POST /v1/emails/read?userId=123
Content-Type: application/json

{
  "provider": "google",
  "emailId": "email_id_here",
  "format": "full",
  "includeAttachments": true
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "email_id",
    "threadId": "thread_id",
    "subject": "Email Konusu",
    "from": "sender@example.com",
    "to": ["recipient@example.com"],
    "cc": ["cc@example.com"],
    "bcc": ["bcc@example.com"],
    "body": "Email içeriği",
    "contentType": "text/html",
    "receivedDate": "2024-01-15T10:30:00",
    "sentDate": "2024-01-15T10:25:00",
    "isRead": false,
    "isStarred": false,
    "isImportant": false,
    "labels": ["INBOX", "UNREAD"],
    "attachments": [
      {
        "fileName": "document.pdf",
        "contentType": "application/pdf",
        "size": 1024000
      }
    ],
    "snippet": "Email özeti...",
    "provider": "google"
  },
  "message": "Email read successfully"
}
```

### Email Listesi

```http
POST /v1/emails/list?userId=123
Content-Type: application/json

{
  "provider": "google",
  "label": "INBOX",
  "query": "from:example.com",
  "maxResults": 50,
  "pageToken": "next_page_token",
  "includeSpamTrash": false,
  "orderBy": "internalDate",
  "sortOrder": "descending"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "emails": [
      {
        "id": "email_id_1",
        "threadId": "thread_id_1",
        "subject": "Email Konusu 1",
        "from": "sender1@example.com",
        "snippet": "Email özeti 1...",
        "receivedDate": "2024-01-15T10:30:00",
        "isRead": false,
        "isStarred": false,
        "isImportant": false,
        "labels": ["INBOX", "UNREAD"],
        "hasAttachments": true
      }
    ],
    "nextPageToken": "next_page_token",
    "resultSizeEstimate": 150,
    "provider": "google",
    "label": "INBOX",
    "totalCount": 150
  },
  "message": "Email list retrieved successfully"
}
```

### Email Silme

```http
DELETE /v1/emails/delete?userId=123
Content-Type: application/json

{
  "provider": "google",
  "emailIds": ["email_id_1", "email_id_2"],
  "permanentDelete": false
}
```

### Email Etiketleme

```http
# Okundu olarak işaretle
PUT /v1/emails/mark-read?userId=123&provider=google
Content-Type: application/json

["email_id_1", "email_id_2"]

# Okunmadı olarak işaretle
PUT /v1/emails/mark-unread?userId=123&provider=google
Content-Type: application/json

["email_id_1", "email_id_2"]

# Yıldızlı yap
PUT /v1/emails/star?userId=123&provider=google
Content-Type: application/json

["email_id_1", "email_id_2"]

# Yıldızı kaldır
PUT /v1/emails/unstar?userId=123&provider=google
Content-Type: application/json

["email_id_1", "email_id_2"]
```

---

## 💡 Kullanım Örnekleri

### JavaScript/TypeScript

```javascript
// Email gönderme
const sendEmail = async () => {
  const response = await fetch('/v1/emails/send?userId=123', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify({
      provider: 'google',
      fromEmail: 'user@gmail.com',
      toEmails: ['recipient@example.com'],
      subject: 'Test Email',
      body: '<h1>Merhaba!</h1><p>Bu bir test emailidir.</p>',
      contentType: 'text/html'
    })
  });
  
  const result = await response.json();
  console.log('Email sent:', result);
};

// Email listesi alma
const getEmails = async () => {
  const response = await fetch('/v1/emails/list?userId=123', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify({
      provider: 'google',
      label: 'INBOX',
      maxResults: 20,
      orderBy: 'internalDate',
      sortOrder: 'descending'
    })
  });
  
  const result = await response.json();
  console.log('Emails:', result.data.emails);
};

// Email okuma
const readEmail = async (emailId) => {
  const response = await fetch('/v1/emails/read?userId=123', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify({
      provider: 'google',
      emailId: emailId,
      format: 'full',
      includeAttachments: true
    })
  });
  
  const result = await response.json();
  console.log('Email content:', result.data);
};

// Email silme
const deleteEmails = async (emailIds) => {
  const response = await fetch('/v1/emails/delete?userId=123', {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify({
      provider: 'google',
      emailIds: emailIds,
      permanentDelete: false
    })
  });
  
  const result = await response.json();
  console.log('Emails deleted:', result);
};

// Email etiketleme
const markAsRead = async (emailIds) => {
  const response = await fetch('/v1/emails/mark-read?userId=123&provider=google', {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + jwtToken
    },
    body: JSON.stringify(emailIds)
  });
  
  const result = await response.json();
  console.log('Emails marked as read:', result);
};
```

### Python

```python
import requests
import json

# Email gönderme
def send_email():
    url = "http://localhost:8080/v1/emails/send"
    params = {"userId": 123}
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {jwt_token}"
    }
    
    data = {
        "provider": "google",
        "fromEmail": "user@gmail.com",
        "toEmails": ["recipient@example.com"],
        "subject": "Test Email",
        "body": "<h1>Merhaba!</h1><p>Bu bir test emailidir.</p>",
        "contentType": "text/html"
    }
    
    response = requests.post(url, params=params, headers=headers, json=data)
    result = response.json()
    print("Email sent:", result)

# Email listesi alma
def get_emails():
    url = "http://localhost:8080/v1/emails/list"
    params = {"userId": 123}
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {jwt_token}"
    }
    
    data = {
        "provider": "google",
        "label": "INBOX",
        "maxResults": 20,
        "orderBy": "internalDate",
        "sortOrder": "descending"
    }
    
    response = requests.post(url, params=params, headers=headers, json=data)
    result = response.json()
    print("Emails:", result["data"]["emails"])
```

---

## 🔒 Güvenlik

### OAuth Güvenliği

- **Scope Validation:** Minimum gerekli izinler
- **Token Security:** Güvenli token saklama
- **Token Refresh:** Otomatik token yenileme
- **Access Control:** Kullanıcı bazlı erişim kontrolü

### API Güvenliği

- **Authentication:** JWT token ile kimlik doğrulama
- **Authorization:** Role-based access control
- **Rate Limiting:** API rate limiting
- **Input Validation:** Tüm input'lar doğrulanır

### Veri Güvenliği

- **Encryption:** Hassas veriler şifrelenir
- **Audit Trail:** Tüm işlemler loglanır
- **Error Handling:** Güvenli hata yönetimi
- **Data Sanitization:** Input sanitization

---

## 🔧 Troubleshooting

### Yaygın Sorunlar

#### 1. "OAuth Token Expired" Hatası
**Semptom:** Email işlemlerinde token hatası
**Çözüm:**
- Token'ın yenilenmesi gerekir
- OAuth bağlantısını yeniden kurun
- Token cleanup scheduler'ı kontrol edin

#### 2. "Gmail API Quota Exceeded" Hatası
**Semptom:** Gmail API limit aşımı
**Çözüm:**
- API kullanımını azaltın
- Rate limiting uygulayın
- Quota monitoring ekleyin

#### 3. "Email Not Found" Hatası
**Semptom:** Email ID bulunamadı
**Çözüm:**
- Email ID'nin doğru olduğundan emin olun
- Email'in silinip silinmediğini kontrol edin
- Provider'ı doğrulayın

#### 4. "Permission Denied" Hatası
**Semptom:** Email işlemlerinde izin hatası
**Çözüm:**
- OAuth scope'larını kontrol edin
- Kullanıcı izinlerini doğrulayın
- Provider ayarlarını kontrol edin

### Debug Modu

```properties
# Debug logging
logging.level.com.prospect.crm.service.EmailService=DEBUG
logging.level.com.prospect.crm.controller.EmailController=DEBUG
logging.level.org.springframework.web.client.RestTemplate=DEBUG
```

### Test Endpoint'leri

```http
# Email gönderme test
POST /v1/emails/send?userId=123
{
  "provider": "google",
  "fromEmail": "test@gmail.com",
  "toEmails": ["test@example.com"],
  "subject": "Test",
  "body": "Test email"
}

# Email listesi test
POST /v1/emails/list?userId=123
{
  "provider": "google",
  "label": "INBOX",
  "maxResults": 10
}
```

---

## 📋 Best Practices

### 1. Performance

- ✅ **Batch Operations:** Toplu işlemler için batch API'leri kullanın
- ✅ **Pagination:** Büyük listeler için sayfalama kullanın
- ✅ **Caching:** Sık kullanılan verileri cache'leyin
- ✅ **Async Processing:** Uzun süren işlemleri asenkron yapın

### 2. Error Handling

- ✅ **Retry Logic:** Geçici hatalar için retry mekanizması
- ✅ **Fallback:** OAuth başarısız olursa SMTP kullanın
- ✅ **Graceful Degradation:** Hata durumunda kısmi fonksiyonalite
- ✅ **User Feedback:** Kullanıcıya anlamlı hata mesajları

### 3. Security

- ✅ **Token Management:** Token'ları güvenli saklayın
- ✅ **Scope Minimization:** Minimum gerekli scope'ları kullanın
- ✅ **Input Validation:** Tüm input'ları doğrulayın
- ✅ **Audit Logging:** Tüm işlemleri loglayın

### 4. User Experience

- ✅ **Loading States:** İşlem durumunu gösterin
- ✅ **Progress Indicators:** Uzun işlemler için progress bar
- ✅ **Error Messages:** Kullanıcı dostu hata mesajları
- ✅ **Confirmation Dialogs:** Kritik işlemler için onay

---

## 📚 Ek Kaynaklar

### API Dokümantasyonu
- [Gmail API Reference](https://developers.google.com/gmail/api/reference/rest)
- [Microsoft Graph API Reference](https://docs.microsoft.com/en-us/graph/api/resources/mail-api-overview)

### OAuth Dokümantasyonu
- [Gmail OAuth2](https://developers.google.com/gmail/api/auth/about-auth)
- [Microsoft OAuth2](https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-oauth2-auth-code-flow)

### Güvenlik
- [OAuth2 Security Best Practices](https://oauth.net/2/oauth-best-practice/)
- [Email Security Guidelines](https://www.ietf.org/rfc/rfc5321.txt)

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