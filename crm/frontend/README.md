# Prospect CRM Frontend

Prospect CRM sisteminin React tabanlı frontend uygulaması.

## 🚀 Özellikler

- **🔐 JWT Authentication**: Access ve Refresh token sistemi
- **🔗 OAuth Integration**: Google ve Microsoft entegrasyonu
- **👥 Role-Based Access Control**: Admin ve User rolleri
- **📧 Email Management**: Gelen kutusu, giden kutusu, taslaklar
- **🎯 Lead Management**: Lead listesi ve detayları
- **📊 Admin Dashboard**: Grafikler ve istatistikler
- **🤖 Robot Monitoring**: Robot durumları ve çalışma saatleri
- **📝 Log Management**: Sistem logları ve uyarılar

## 🛠 Teknoloji Stack

- **React 18** - UI Framework
- **TypeScript** - Type Safety
- **Vite** - Build Tool
- **Ant Design** - UI Components
- **Tailwind CSS** - Styling
- **React Router** - Routing
- **Axios** - HTTP Client
- **Recharts** - Charts

## 📋 Gereksinimler

- **Node.js 18+**
- **npm** veya **yarn**

## 🚀 Kurulum

### 1. Bağımlılıkları Yükleyin

```bash
npm install
```

### 2. Geliştirme Sunucusunu Başlatın

```bash
npm run dev
```

Uygulama `http://localhost:5173` adresinde çalışacaktır.

### 3. Production Build

```bash
npm run build
```

## 🔧 Konfigürasyon

### API URL

`src/services/api.ts` dosyasında API base URL'ini güncelleyin:

```typescript
const API_BASE_URL = 'http://localhost:8080/v1';
```

### Environment Variables

`.env` dosyası oluşturun:

```env
VITE_API_URL=http://localhost:8080/v1
VITE_APP_NAME=Prospect CRM
```

## 📁 Proje Yapısı

```
src/
├── components/          # Yeniden kullanılabilir bileşenler
│   ├── Layout.tsx      # Ana layout
│   ├── LeadDetail.tsx  # Lead detay modalı
│   ├── LeadForm.tsx    # Lead form
│   ├── EmailDetail.tsx # Email detay modalı
│   ├── EmailForm.tsx   # Email gönderme formu
│   └── EmailDraftForm.tsx # Email taslak formu
├── context/            # React Context'ler
│   └── AuthContext.tsx # Authentication context
├── pages/              # Sayfa bileşenleri
│   ├── Login.tsx       # Giriş sayfası
│   ├── Register.tsx    # Kayıt sayfası
│   ├── AdminDashboard.tsx # Admin dashboard
│   ├── Leads.tsx       # Lead listesi
│   └── Emails.tsx      # Email yönetimi
├── services/           # API servisleri
│   └── api.ts         # API client ve endpoint'ler
├── types/              # TypeScript tip tanımları
│   └── index.ts       # Tüm tip tanımları
├── App.tsx            # Ana uygulama bileşeni
├── main.tsx           # Uygulama giriş noktası
└── index.css          # Global stiller
```

## 🔐 Authentication

### Login Flow

1. Kullanıcı email/şifre ile giriş yapar
2. JWT access ve refresh token alınır
3. Token'lar localStorage'da saklanır
4. Her API isteğinde access token header'a eklenir
5. Token süresi dolduğunda otomatik yenilenir

### OAuth Flow

1. Kullanıcı Google/Microsoft ile giriş yapar
2. OAuth callback ile token alınır
3. Backend'de user bilgileri kontrol edilir
4. Mevcut kullanıcı giriş yapar / yeni kullanıcı kaydolur

## 📧 Email Management

### Gelen Kutusu
- Email listesi
- Email detayları
- Email silme
- Okundu işaretleme

### Giden Kutusu
- Gönderilen email listesi
- Email detayları
- Email silme

### Taslaklar
- Email taslakları
- Robot oluşturulan taslaklar
- Taslak gönderme
- Taslak düzenleme

## 🎯 Lead Management

### Lead Listesi
- Tüm leadler
- Arama ve filtreleme
- Durum bazlı filtreleme
- Sayfalama

### Lead Detayları
- İletişim bilgileri
- Şirket bilgileri
- Notlar
- İstatistikler

### Lead İşlemleri
- Yeni lead oluşturma
- Lead düzenleme
- Lead silme
- Durum güncelleme

## 📊 Admin Dashboard

### İstatistikler
- Toplam kullanıcı sayısı
- Aktif abonelik sayısı
- Toplam lead sayısı
- Bugün gönderilen email sayısı

### Grafikler
- Aylık kullanıcı artışı
- Lead dönüşüm oranları
- Email gönderim istatistikleri

### Robot Durumları
- Email Robot
- Lead Robot
- Bounce Robot
- Son çalışma saatleri
- Aktiflik durumları

### Log Yönetimi
- Uyarı logları
- Hata logları
- Güvenlik logları
- Log temizleme

## 🎨 UI Components

### Ant Design
- Form bileşenleri
- Tablo bileşenleri
- Modal bileşenleri
- Navigation bileşenleri

### Tailwind CSS
- Responsive tasarım
- Custom stiller
- Utility classes

### Recharts
- Line charts
- Pie charts
- Bar charts
- Area charts

## 🔧 Development

### Code Style
- TypeScript strict mode
- ESLint kuralları
- Prettier formatting
- Component-based architecture

### State Management
- React Context API
- Local state (useState)
- Effect hooks (useEffect)

### API Integration
- Axios interceptors
- Error handling
- Loading states
- Token management

## 🚀 Deployment

### Build
```bash
npm run build
```

### Production
```bash
npm run preview
```

### Docker
```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "run", "preview"]
```

## 📝 Notlar

- Backend API'si `http://localhost:8080` adresinde çalışmalıdır
- CORS ayarları backend'de yapılandırılmalıdır
- OAuth provider'ları backend'de yapılandırılmalıdır
- JWT secret'ları güvenli şekilde saklanmalıdır

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun
3. Değişikliklerinizi commit edin
4. Branch'inizi push edin
5. Pull Request oluşturun

## 📄 Lisans

MIT License 