# Test Endpoints Documentation

## 🔧 Security Configuration
Spring Security şu anda test için devre dışı bırakılmıştır. Tüm `/v1/**` endpoint'leri authentication gerektirmeden erişilebilir.

## 🚀 Test Endpoints

### Health Check Endpoints
```bash
# Health check
GET http://localhost:8080/v1/health

# Ping test
GET http://localhost:8080/v1/health/ping
```

### Test Response Endpoints
```bash
# Success response test
GET http://localhost:8080/v1/test/success

# Data response test
GET http://localhost:8080/v1/test/data

# Pagination response test
GET http://localhost:8080/v1/test/pagination

# Error response test
GET http://localhost:8080/v1/test/error

# Validation error test
GET http://localhost:8080/v1/test/validation-error

# Custom error test
GET http://localhost:8080/v1/test/custom-error
```

### User Endpoints
```bash
# Get all users
GET http://localhost:8080/v1/users

# Get user by ID
GET http://localhost:8080/v1/users/{id}

# Create user
POST http://localhost:8080/v1/users
Content-Type: application/json

{
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "phone": "5551234567",
  "username": "johndoe",
  "password": "Password123",
  "roleId": 1001,
  "subscriptionId": 1001
}

# Update user
PUT http://localhost:8080/v1/users/{id}
Content-Type: application/json

{
  "id": 1001,
  "name": "John Updated",
  "surname": "Doe",
  "email": "john.updated@example.com",
  "phone": "5551234567",
  "username": "johndoe",
  "password": "NewPassword123",
  "roleId": 1001,
  "subscriptionId": 1001
}

# Delete user
DELETE http://localhost:8080/v1/users/{id}
```

## 📊 Expected Responses

### ✅ Success Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "success": true,
  "message": "Operation completed successfully",
  "data": "Test successful",
  "error": null,
  "pagination": null
}
```

### ❌ Error Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "success": false,
  "message": "Username already exists",
  "data": null,
  "error": {
    "code": "ERR_2003",
    "message": "Username already exists",
    "details": "Username 'johndoe' is already taken",
    "field": "username",
    "value": "johndoe"
  },
  "pagination": null
}
```

### 📄 Pagination Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "success": true,
  "message": "Users retrieved successfully",
  "data": [...],
  "error": null,
  "pagination": {
    "page": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3,
    "hasNext": true,
    "hasPrevious": false,
    "isFirst": true,
    "isLast": false
  }
}
```

## 🧪 Testing Tools

### cURL Examples
```bash
# Health check
curl -X GET http://localhost:8080/v1/health

# Test success
curl -X GET http://localhost:8080/v1/test/success

# Test error
curl -X GET http://localhost:8080/v1/test/error
```

### Postman Collection
```json
{
  "info": {
    "name": "Prospect CRM API Tests",
    "description": "Test endpoints for Prospect CRM API"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/v1/health"
      }
    },
    {
      "name": "Test Success",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/v1/test/success"
      }
    },
    {
      "name": "Test Error",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/v1/test/error"
      }
    }
  ]
}
```

## 🔍 Troubleshooting

### 401 Unauthorized Error
Eğer hala 401 hatası alıyorsanız:
1. Uygulamayı yeniden başlatın
2. SecurityConfig sınıfının doğru yüklendiğinden emin olun
3. Log'ları kontrol edin: `logging.level.org.springframework.security=DEBUG`

### 404 Not Found Error
1. Uygulamanın çalıştığından emin olun
2. Port numarasını kontrol edin (8080)
3. Endpoint URL'lerini kontrol edin

### 500 Internal Server Error
1. Database bağlantısını kontrol edin
2. PostgreSQL'in çalıştığından emin olun
3. Log'ları kontrol edin

## 📝 Notes

- Tüm endpoint'ler `/v1/` prefix'i ile başlar
- JSON response'lar standart ApiResponse formatında
- Error code'lar `ERR_` prefix'i ile başlar
- Test endpoint'leri authentication gerektirmez 