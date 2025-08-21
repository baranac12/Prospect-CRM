# Email Draft Management - Detaylı Rehber

## Genel Bakış

Email Draft Management sistemi, kullanıcıların email taslaklarını oluşturmasına, düzenlemesine, yönetmesine ve göndermesine olanak sağlayan kapsamlı bir sistemdir. Bu sistem, özellikle robot tarafından oluşturulan taslakların kullanıcı tarafından onaylanması ve gönderilmesi için tasarlanmıştır.

## Özellikler

### 🔧 Temel Özellikler
- **Taslak Oluşturma**: Yeni email taslakları oluşturma
- **Taslak Düzenleme**: Mevcut taslakları düzenleme
- **Taslak Silme**: Taslakları silme
- **Taslak Gönderme**: Taslakları email olarak gönderme
- **Taslak Listeleme**: Filtreleme ve sayfalama ile taslak listesi
- **İstatistikler**: Taslak istatistiklerini görüntüleme

### 📧 Email Özellikleri
- **Çoklu Alıcı**: TO, CC, BCC alanları
- **Ekler**: Dosya ekleri desteği
- **İçerik Türü**: Text/Plain ve Text/HTML desteği
- **Şablon Desteği**: Email şablonları kullanımı
- **Sağlayıcı Seçimi**: Gmail, Outlook, SMTP desteği

### 🔍 Filtreleme ve Arama
- **Durum Filtreleme**: DRAFT, SENT, CANCELLED
- **Lead Filtreleme**: Belirli lead'e ait taslaklar
- **Sağlayıcı Filtreleme**: Gmail, Outlook, SMTP
- **Şablon Filtreleme**: Belirli şablona ait taslaklar
- **Robot Filtreleme**: Robot tarafından oluşturulan taslaklar
- **Metin Arama**: Konu, içerik ve alıcılarda arama

## Veritabanı Yapısı

### EmailDraft Entity

```sql
CREATE TABLE email_drafts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    lead_id BIGINT REFERENCES leads(id),
    subject VARCHAR(255),
    body TEXT,
    content_type VARCHAR(50), -- text/plain, text/html
    to_emails TEXT, -- Comma-separated email addresses
    cc_emails TEXT, -- Comma-separated email addresses
    bcc_emails TEXT, -- Comma-separated email addresses
    attachments TEXT, -- JSON string of attachment info
    created_by_robot BOOLEAN DEFAULT false,
    status VARCHAR(20), -- DRAFT, SENT, CANCELLED
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    sent_at TIMESTAMP,
    provider VARCHAR(20), -- GOOGLE, MICROSOFT, SMTP
    template_name VARCHAR(100),
    template_data TEXT -- JSON string of template variables
);
```

## API Endpoints

### 🔧 CRUD İşlemleri

#### 1. Taslak Oluşturma
```http
POST /v1/email-drafts?userId={userId}
Content-Type: application/json

{
    "leadId": 1001,
    "subject": "Test Email",
    "body": "This is a test email body",
    "contentType": "text/html",
    "toEmails": ["recipient@example.com"],
    "ccEmails": ["cc@example.com"],
    "bccEmails": ["bcc@example.com"],
    "attachments": [
        {
            "fileName": "document.pdf",
            "contentType": "application/pdf",
            "content": "base64EncodedContent"
        }
    ],
    "provider": "GOOGLE",
    "templateName": "welcome_template",
    "templateData": "{\"name\":\"John\",\"company\":\"ABC Corp\"}"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Email draft created successfully",
    "data": {
        "id": 1001,
        "leadId": 1001,
        "subject": "Test Email",
        "body": "This is a test email body",
        "contentType": "text/html",
        "toEmails": ["recipient@example.com"],
        "ccEmails": ["cc@example.com"],
        "bccEmails": ["bcc@example.com"],
        "attachments": [...],
        "createdByRobot": false,
        "status": "DRAFT",
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00",
        "sentAt": null,
        "provider": "GOOGLE",
        "templateName": "welcome_template",
        "templateData": "{\"name\":\"John\",\"company\":\"ABC Corp\"}"
    }
}
```

#### 2. Taslak Getirme
```http
GET /v1/email-drafts/{draftId}?userId={userId}
```

#### 3. Taslak Güncelleme
```http
PUT /v1/email-drafts/{draftId}?userId={userId}
Content-Type: application/json

{
    "subject": "Updated Subject",
    "body": "Updated body content",
    "toEmails": ["newrecipient@example.com"]
}
```

#### 4. Taslak Silme
```http
DELETE /v1/email-drafts/{draftId}?userId={userId}
```

### 📋 Liste İşlemleri

#### 5. Taslak Listeleme
```http
POST /v1/email-drafts/list?userId={userId}
Content-Type: application/json

{
    "status": "DRAFT",
    "leadId": 1001,
    "provider": "GOOGLE",
    "templateName": "welcome_template",
    "createdByRobot": false,
    "searchTerm": "test",
    "sortBy": "updatedAt",
    "sortOrder": "DESC",
    "page": 0,
    "size": 20
}
```

**Response:**
```json
{
    "success": true,
    "message": "Email drafts retrieved successfully",
    "data": {
        "drafts": [
            {
                "id": 1001,
                "leadId": 1001,
                "subject": "Test Email",
                "body": "This is a test email body",
                "contentType": "text/html",
                "toEmails": ["recipient@example.com"],
                "ccEmails": [],
                "bccEmails": [],
                "createdByRobot": false,
                "status": "DRAFT",
                "provider": "GOOGLE",
                "templateName": "welcome_template",
                "createdAt": "2024-01-15T10:30:00",
                "updatedAt": "2024-01-15T10:30:00",
                "sentAt": null,
                "hasAttachments": false
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "currentPage": 0,
        "pageSize": 20,
        "hasNext": false,
        "hasPrevious": false
    }
}
```

### 📤 Gönderme İşlemleri

#### 6. Taslak Gönderme
```http
POST /v1/email-drafts/{draftId}/send?userId={userId}
```

**Response:**
```json
{
    "success": true,
    "message": "Email draft sent successfully",
    "data": "Draft sent successfully"
}
```

### 📊 İstatistik İşlemleri

#### 7. Taslak İstatistikleri
```http
GET /v1/email-drafts/stats?userId={userId}
```

**Response:**
```json
{
    "success": true,
    "message": "Draft statistics retrieved successfully",
    "data": {
        "totalDrafts": 5,
        "totalSent": 3,
        "totalDraftsAll": 8
    }
}
```

## Kullanım Senaryoları

### 1. Robot Tarafından Oluşturulan Taslaklar

```javascript
// Robot tarafından taslak oluşturma
const draftRequest = {
    leadId: 1001,
    subject: "Welcome to our platform",
    body: "Dear {{name}}, welcome to our platform!",
    contentType: "text/html",
    toEmails: ["lead@example.com"],
    createdByRobot: true,
    templateName: "welcome_template",
    templateData: JSON.stringify({
        name: "John Doe",
        company: "ABC Corp"
    }),
    provider: "GOOGLE"
};

// Taslak oluştur
const response = await fetch('/v1/email-drafts?userId=1001', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(draftRequest)
});
```

### 2. Kullanıcı Tarafından Taslak Onaylama

```javascript
// Robot tarafından oluşturulan taslakları listele
const listRequest = {
    createdByRobot: true,
    status: "DRAFT",
    sortBy: "createdAt",
    sortOrder: "DESC"
};

const drafts = await fetch('/v1/email-drafts/list?userId=1001', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(listRequest)
});

// Taslağı onayla ve gönder
await fetch(`/v1/email-drafts/${draftId}/send?userId=1001`, {
    method: 'POST'
});
```

### 3. Manuel Taslak Oluşturma

```javascript
// Manuel taslak oluşturma
const manualDraft = {
    subject: "Follow-up Email",
    body: "Hi {{name}}, I wanted to follow up on our conversation...",
    contentType: "text/html",
    toEmails: ["contact@example.com"],
    ccEmails: ["manager@example.com"],
    attachments: [
        {
            fileName: "proposal.pdf",
            contentType: "application/pdf",
            content: "base64EncodedContent"
        }
    ],
    provider: "OUTLOOK"
};

await fetch('/v1/email-drafts?userId=1001', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(manualDraft)
});
```

## Güvenlik

### 🔐 Yetkilendirme
- Tüm endpointler `USER` rolü gerektirir
- Kullanıcılar sadece kendi taslaklarına erişebilir
- Taslak sahipliği kontrolü yapılır

### 🛡️ Veri Doğrulama
- Email formatı doğrulaması
- Dosya boyutu ve türü kontrolü
- XSS koruması
- SQL Injection koruması

## Hata Kodları

| Kod | Açıklama |
|-----|----------|
| ERR_7001 | Email draft not found |
| ERR_7005 | Email draft already exists |
| ERR_7007 | Failed to create email draft |
| ERR_7008 | Failed to update email draft |
| ERR_7009 | Failed to delete email draft |
| ERR_7010 | Failed to retrieve email draft |
| ERR_7011 | Failed to list email drafts |
| ERR_7012 | Failed to send email draft |
| ERR_7013 | Email draft already sent |

## En İyi Uygulamalar

### 📝 Taslak Yönetimi
1. **Düzenli Temizlik**: Eski taslakları düzenli olarak temizleyin
2. **Şablon Kullanımı**: Tekrar eden içerikler için şablonlar kullanın
3. **Versiyonlama**: Önemli değişiklikler için taslak kopyalayın
4. **Etiketleme**: Taslakları kategorilere ayırın

### 📧 Email İçeriği
1. **Konu Satırı**: Açık ve etkili konu satırları kullanın
2. **Kişiselleştirme**: Alıcı adı ve şirket bilgilerini kullanın
3. **Çağrı-Eylem**: Net çağrı-eylem butonları ekleyin
4. **Mobil Uyumluluk**: Responsive tasarım kullanın

### 🔄 İş Akışı
1. **Robot Entegrasyonu**: Robot tarafından oluşturulan taslakları otomatik onaylayın
2. **Onay Süreci**: Kritik taslaklar için çoklu onay süreci uygulayın
3. **Zamanlama**: Optimal gönderim zamanlarını belirleyin
4. **Takip**: Gönderilen taslakların durumunu takip edin

## Sorun Giderme

### Yaygın Sorunlar

#### 1. Taslak Gönderilemiyor
- **Sebep**: OAuth token süresi dolmuş
- **Çözüm**: OAuth token'ı yenileyin

#### 2. Ekler Yüklenemiyor
- **Sebep**: Dosya boyutu çok büyük
- **Çözüm**: Dosya boyutunu kontrol edin

#### 3. Şablon Render Edilemiyor
- **Sebep**: Template data JSON formatında değil
- **Çözüm**: JSON formatını kontrol edin

### Debug İpuçları
1. **Log Kontrolü**: SystemLog tablosunu kontrol edin
2. **OAuth Durumu**: OAuth token durumunu kontrol edin
3. **Email Sağlayıcısı**: Gmail/Outlook bağlantısını test edin
4. **Veritabanı**: EmailDraft tablosunu kontrol edin

## Gelecek Geliştirmeler

### 🚀 Planlanan Özellikler
- **Toplu İşlemler**: Çoklu taslak seçimi ve işlemi
- **Zamanlama**: Gelecek tarihli gönderim
- **A/B Testi**: Farklı versiyonları test etme
- **Analitik**: Detaylı gönderim istatistikleri
- **Otomatik Kaydetme**: Otomatik taslak kaydetme
- **Şablon Editörü**: Görsel şablon editörü

### 🔧 Teknik İyileştirmeler
- **Cache**: Redis cache entegrasyonu
- **Queue**: Asenkron gönderim kuyruğu
- **Webhook**: Gerçek zamanlı durum güncellemeleri
- **API Rate Limiting**: Gelişmiş rate limiting
- **Monitoring**: Prometheus metrikleri

## Destek

### 📞 Teknik Destek
- **Email**: support@prospectcrm.com
- **Dokümantasyon**: https://docs.prospectcrm.com
- **GitHub**: https://github.com/prospectcrm

### 📚 Ek Kaynaklar
- [Email Management Guide](./EMAIL_MANAGEMENT.md)
- [OAuth Integration Guide](./OAUTH_INTEGRATION.md)
- [JWT Authentication Guide](./JWT_DETAILED_GUIDE.md)
- [API Reference Documentation](./API_REFERENCE.md) 