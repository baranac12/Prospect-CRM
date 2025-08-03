# Docker Troubleshooting Rehberi

## 🚨 "load metadata for docker.io/library/openjdk:17-jre-alpine" Hatası

### **Hata Açıklaması**
Bu hata, Docker image'ını çekerken metadata yüklenirken oluşur. Genellikle network, cache veya image registry sorunlarından kaynaklanır.

### **Çözüm Yöntemleri**

#### **1. Docker Cache'ini Temizle**
```bash
# Tüm Docker cache'ini temizle
docker system prune -a -f

# Sadece build cache'ini temizle
docker builder prune -a -f
```

#### **2. Docker Daemon'u Yeniden Başlat**
```bash
# Windows
net stop docker
net start docker

# Linux/Mac
sudo systemctl restart docker
```

#### **3. Network Bağlantısını Kontrol Et**
```bash
# Docker Hub'a bağlantıyı test et
docker pull hello-world

# DNS ayarlarını kontrol et
nslookup docker.io
```

#### **4. Alternatif Base Image Kullan**
```dockerfile
# Orijinal (sorunlu)
FROM openjdk:17-jre-alpine

# Alternatif 1
FROM amazoncorretto:17-alpine

# Alternatif 2
FROM eclipse-temurin:17-jre-alpine

# Alternatif 3
FROM adoptopenjdk:17-jre-hotspot
```

#### **5. Docker Build'i Yeniden Dene**
```bash
# Force rebuild
docker-compose build --no-cache

# Sadece CRM app'i rebuild et
docker-compose build --no-cache crm-app
```

### **Alternatif Çözümler**

#### **1. Local Build Kullan**
```bash
# Önce local'de build et
mvn clean package -DskipTests

# Sonra Dockerfile.simple kullan
docker build -f Dockerfile.simple -t crm-app .
```

#### **2. Farklı Dockerfile Kullan**
```bash
# Dockerfile.simple kullan
docker-compose -f docker-compose.yml up -d --build
```

#### **3. Manual Image Pull**
```bash
# Image'ı manuel olarak çek
docker pull amazoncorretto:17-alpine
docker pull eclipse-temurin:17-jre-alpine
```

### **Environment-Specific Çözümler**

#### **Windows WSL2**
```bash
# WSL2'de Docker'ı yeniden başlat
wsl --shutdown
wsl --start

# Docker Desktop'ı yeniden başlat
```

#### **Corporate Network**
```bash
# Proxy ayarları
export HTTP_PROXY=http://proxy.company.com:8080
export HTTPS_PROXY=http://proxy.company.com:8080

# Docker daemon proxy ayarları
# /etc/docker/daemon.json
{
  "proxies": {
    "http-proxy": "http://proxy.company.com:8080",
    "https-proxy": "http://proxy.company.com:8080"
  }
}
```

#### **Firewall Issues**
```bash
# Windows Firewall'da Docker'a izin ver
netsh advfirewall firewall add rule name="Docker" dir=in action=allow protocol=TCP

# Antivirus software'i geçici olarak devre dışı bırak
```

### **Debug Komutları**

#### **1. Docker Info**
```bash
# Docker sistem bilgileri
docker info

# Docker version
docker version
```

#### **2. Network Test**
```bash
# Docker Hub connectivity
curl -I https://registry-1.docker.io/v2/

# DNS resolution
nslookup registry-1.docker.io
```

#### **3. Build Debug**
```bash
# Verbose build
docker-compose build --progress=plain

# Build with debug info
DOCKER_BUILDKIT=0 docker-compose build
```

### **Prevention Strategies**

#### **1. Reliable Base Images**
```dockerfile
# Önerilen base images
FROM amazoncorretto:17-alpine
FROM eclipse-temurin:17-jre-alpine
FROM adoptopenjdk:17-jre-hotspot
```

#### **2. Build Caching**
```yaml
# docker-compose.yml
services:
  crm-app:
    build:
      context: .
      dockerfile: Dockerfile
      args:
        - BUILDKIT_INLINE_CACHE=1
```

#### **3. Health Checks**
```yaml
# docker-compose.yml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/v1/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

### **Emergency Solutions**

#### **1. Minimal Dockerfile**
```dockerfile
FROM openjdk:17-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **2. Local Development**
```bash
# Sadece database'i Docker'da çalıştır
docker-compose up -d postgres redis

# CRM'i local'de çalıştır
mvn spring-boot:run
```

#### **3. Alternative Approach**
```bash
# JAR dosyasını hazırla
mvn clean package -DskipTests

# Docker olmadan test et
java -jar target/crm-0.0.1-SNAPSHOT.jar
```

### **Success Indicators**

✅ **Başarılı Build**
```bash
# Bu komutlar çalışmalı
docker-compose up -d
docker-compose ps
curl http://localhost:8080/v1/health
```

✅ **Container Status**
```bash
# Tüm container'lar "Up" durumunda olmalı
docker-compose ps
```

✅ **Health Checks**
```bash
# Health check'ler başarılı olmalı
docker-compose exec crm-app curl -f http://localhost:8080/v1/health
```

### **Next Steps**

1. **Cache temizle** → `docker system prune -a -f`
2. **Dockerfile güncelle** → Amazon Corretto kullan
3. **Yeniden build et** → `docker-compose build --no-cache`
4. **Test et** → `docker-compose up -d`

Bu rehber ile Docker sorunlarını çözebilirsiniz! 🚀 