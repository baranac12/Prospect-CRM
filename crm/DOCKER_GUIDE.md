# Docker Compose Kullanım Rehberi

## 🐳 Docker Compose Nedir?

Docker Compose, birden fazla Docker container'ını tek bir dosyada tanımlayarak birlikte çalıştırmanızı sağlayan bir araçtır.

## 📁 Proje Yapısı

```
crm/
├── docker-compose.yml          # Ana Docker Compose dosyası
├── Dockerfile                  # CRM uygulaması için Docker image
├── .dockerignore              # Docker build'de hariç tutulacak dosyalar
├── nginx/
│   └── nginx.conf            # Nginx reverse proxy konfigürasyonu
├── src/main/resources/
│   └── application-docker.properties  # Docker için Spring Boot ayarları
└── DOCKER_GUIDE.md           # Bu dosya
```

## 🚀 Hızlı Başlangıç

### 1. Gereksinimler
```bash
# Docker ve Docker Compose yüklü olmalı
docker --version
docker-compose --version
```

### 2. Uygulamayı Başlatma
```bash
# Tüm servisleri başlat
docker-compose up -d

# Log'ları izle
docker-compose logs -f

# Sadece belirli servisin log'larını izle
docker-compose logs -f crm-app
```

### 3. Uygulamayı Durdurma
```bash
# Tüm servisleri durdur
docker-compose down

# Verileri de sil (dikkatli olun!)
docker-compose down -v
```

## 🔧 Servisler

### 1. PostgreSQL Database
- **Port**: 5432
- **Database**: crm
- **User**: postgres
- **Password**: postgres
- **Volume**: postgres_data

```bash
# Database'e bağlan
docker exec -it crm-postgres psql -U postgres -d crm
```

### 2. Redis Cache (Opsiyonel)
- **Port**: 6379
- **Volume**: redis_data

```bash
# Redis CLI'ya bağlan
docker exec -it crm-redis redis-cli
```

### 3. CRM Application
- **Port**: 8080
- **Health Check**: http://localhost:8080/v1/health

```bash
# Uygulama log'larını izle
docker-compose logs -f crm-app

# Container'a bağlan
docker exec -it crm-application sh
```

### 4. Nginx Reverse Proxy (Opsiyonel)
- **Port**: 80 (HTTP), 443 (HTTPS)
- **Rate Limiting**: API endpoint'leri için
- **SSL**: Production için hazır

## 🛠️ Geliştirme Komutları

### Sadece Database ile Başlat
```bash
# Sadece PostgreSQL'i başlat
docker-compose up -d postgres

# CRM uygulamasını local'de çalıştır
mvn spring-boot:run
```

### Sadece CRM Uygulamasını Yeniden Başlat
```bash
# CRM container'ını yeniden build et ve başlat
docker-compose up -d --build crm-app
```

### Database'i Sıfırla
```bash
# PostgreSQL verilerini sil
docker-compose down -v
docker-compose up -d postgres
```

### Log'ları Temizle
```bash
# Tüm log'ları sil
docker system prune -f
```

## 🔍 Monitoring ve Debug

### Container Durumlarını Kontrol Et
```bash
# Tüm container'ların durumu
docker-compose ps

# Detaylı bilgi
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
```

### Health Check'leri Kontrol Et
```bash
# PostgreSQL health check
docker exec crm-postgres pg_isready -U postgres

# Redis health check
docker exec crm-redis redis-cli ping

# CRM health check
curl http://localhost:8080/v1/health
```

### Resource Kullanımını İzle
```bash
# Container resource kullanımı
docker stats

# Belirli container'ın resource kullanımı
docker stats crm-application
```

## 🧪 Test Komutları

### API Test'leri
```bash
# Health check
curl http://localhost:8080/v1/health

# Test endpoint'leri
curl http://localhost:8080/v1/test/success
curl http://localhost:8080/v1/test/error

# User endpoint'leri
curl http://localhost:8080/v1/users
```

### Database Test
```bash
# PostgreSQL'e bağlan
docker exec -it crm-postgres psql -U postgres -d crm

# Tabloları listele
\dt

# Örnek sorgu
SELECT * FROM users LIMIT 5;
```

## 🔧 Konfigürasyon

### Environment Variables
```bash
# .env dosyası oluştur
cat > .env << EOF
SPRING_PROFILES_ACTIVE=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/crm
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379
EOF
```

### Production Ayarları
```bash
# Production için docker-compose.override.yml oluştur
cat > docker-compose.override.yml << EOF
version: '3.8'
services:
  crm-app:
    environment:
      SPRING_PROFILES_ACTIVE: production
    restart: unless-stopped
  postgres:
    restart: unless-stopped
  redis:
    restart: unless-stopped
EOF
```

## 🚨 Troubleshooting

### Port Çakışması
```bash
# Port'ları kontrol et
netstat -tulpn | grep :8080
netstat -tulpn | grep :5432

# Farklı port kullan
docker-compose up -d -p 8081:8080
```

### Database Bağlantı Hatası
```bash
# PostgreSQL log'larını kontrol et
docker-compose logs postgres

# Database'i yeniden başlat
docker-compose restart postgres
```

### Memory Sorunları
```bash
# Container memory limit'lerini ayarla
docker-compose down
docker-compose up -d --scale crm-app=1
```

### Disk Space Sorunları
```bash
# Kullanılmayan Docker kaynaklarını temizle
docker system prune -a -f

# Volume'ları kontrol et
docker volume ls
docker volume rm $(docker volume ls -q)
```

## 📊 Performance Monitoring

### Resource Monitoring
```bash
# Real-time monitoring
docker stats --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"

# Log monitoring
docker-compose logs -f --tail=100
```

### Database Performance
```bash
# PostgreSQL performance
docker exec -it crm-postgres psql -U postgres -d crm -c "
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats 
WHERE schemaname = 'public'
ORDER BY tablename, attname;
"
```

## 🔒 Security

### SSL Sertifikası Ekle
```bash
# SSL sertifikalarını nginx/ssl/ klasörüne koy
mkdir -p nginx/ssl
cp your-cert.pem nginx/ssl/cert.pem
cp your-key.pem nginx/ssl/key.pem

# HTTPS'i aktif et
# nginx/nginx.conf dosyasındaki HTTPS server bloğunu uncomment et
```

### Firewall Ayarları
```bash
# Sadece gerekli port'ları aç
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw deny 5432/tcp  # Database'i dışarıya kapat
```

## 📝 Best Practices

### 1. Environment Separation
```bash
# Development
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Production
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 2. Backup Strategy
```bash
# Database backup
docker exec crm-postgres pg_dump -U postgres crm > backup.sql

# Restore
docker exec -i crm-postgres psql -U postgres crm < backup.sql
```

### 3. Log Rotation
```bash
# Log rotation için docker-compose.yml'e ekle
services:
  crm-app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## 🎯 Özet

Docker Compose ile CRM uygulamanızı kolayca:
- ✅ **Başlatabilir** (`docker-compose up -d`)
- ✅ **Durdurdurabilir** (`docker-compose down`)
- ✅ **Monitor edebilir** (`docker-compose logs -f`)
- ✅ **Debug edebilir** (`docker exec -it container-name sh`)
- ✅ **Scale edebilir** (`docker-compose up -d --scale crm-app=3`)

Bu rehber ile Docker Compose'u etkili bir şekilde kullanabilirsiniz! 🚀 