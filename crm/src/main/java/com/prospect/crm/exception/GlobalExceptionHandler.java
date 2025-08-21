package com.prospect.crm.exception;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final SystemLogService systemLogService;

    public GlobalExceptionHandler(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        logError("Global Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "An unexpected error occurred",
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        logError("Runtime Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Runtime error occurred",
                        ErrorCode.BAD_REQUEST.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logError("Illegal Argument Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Invalid argument provided",
                        ErrorCode.BAD_REQUEST.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException ex, WebRequest request) {
        logError("Null Pointer Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Null reference error",
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        "A null reference was accessed"
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logWarn("Resource Not Found Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        "Resource not found",
                        ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        logWarn("Bad Request Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Bad request",
                        ErrorCode.BAD_REQUEST.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        logSecurity("Unauthorized Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "Unauthorized",
                        ErrorCode.UNAUTHORIZED.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logSecurity("Authentication Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        "Authentication failed",
                        ErrorCode.INVALID_CREDENTIALS.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailException(EmailException ex, WebRequest request) {
        logError("Email Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Email operation failed",
                        ErrorCode.EMAIL_SEND_FAILED.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentException(PaymentException ex, WebRequest request) {
        logWarn("Payment Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Payment operation failed",
                        ErrorCode.PAYMENT_FAILED.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RobotException.class)
    public ResponseEntity<ApiResponse<Void>> handleRobotException(RobotException ex, WebRequest request) {
        logError("Robot Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Robot operation failed",
                        ErrorCode.ROBOT_FAILED.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiResponse<Void>> handleRateLimitException(RateLimitException ex, WebRequest request) {
        logWarn("Rate Limit Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(
                        "Rate limit exceeded",
                        ErrorCode.RATE_LIMIT_EXCEEDED.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex, WebRequest request) {
        logWarn("Validation Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Validation error",
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<ApiResponse<Void>> handleSubscriptionException(SubscriptionException ex, WebRequest request) {
        logWarn("Subscription Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Subscription error",
                        ErrorCode.SUBSCRIPTION_TYPE_INACTIVE.getCode(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));
        
        logWarn("Method Argument Not Valid Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Validation failed",
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        "Validation errors: " + errors.toString()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        logWarn("Method Argument Type Mismatch Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Invalid parameter type",
                        ErrorCode.BAD_REQUEST.getCode(),
                        "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType().getSimpleName()
                ));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Void>> handleNumberFormatException(NumberFormatException ex, WebRequest request) {
        logWarn("Number Format Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "Number format error",
                        ErrorCode.BAD_REQUEST.getCode(),
                        "Invalid number format provided"
                ));
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<ApiResponse<Void>> handleArrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException ex, WebRequest request) {
        logError("Array Index Out Of Bounds Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Array index out of bounds",
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        "Array access error occurred"
                ));
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<ApiResponse<Void>> handleClassCastException(ClassCastException ex, WebRequest request) {
        logError("Class Cast Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Class cast error",
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        "Type casting error occurred"
                ));
    }

    @ExceptionHandler(OutOfMemoryError.class)
    public ResponseEntity<ApiResponse<Void>> handleOutOfMemoryError(OutOfMemoryError ex, WebRequest request) {
        logError("Out Of Memory Exception", ex, request);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        "Out of memory error",
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        "System is out of memory"
                ));
    }

    // Log metodları
    private void logError(String exceptionType, Exception ex, WebRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            String ipAddress = getIpAddressFromRequest(request);
            String userAgent = getUserAgentFromRequest(request);
            
            systemLogService.logError(
                exceptionType + ": " + ex.getMessage(),
                "Exception occurred in " + ex.getClass().getSimpleName(),
                getStackTraceAsString(ex),
                ex.getClass().getName(),
                "handle" + exceptionType.replace(" ", "")
            );
            
            // Güvenlik logu da kaydet
            systemLogService.logSecurity(
                exceptionType + " occurred",
                "Error details: " + ex.getMessage(),
                userId,
                ipAddress,
                userAgent
            );
        } catch (Exception logEx) {
            log.error("Failed to log exception: {}", logEx.getMessage(), logEx);
        }
    }

    private void logError(String exceptionType, Error ex, WebRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            String ipAddress = getIpAddressFromRequest(request);
            String userAgent = getUserAgentFromRequest(request);
            
            systemLogService.logError(
                exceptionType + ": " + ex.getMessage(),
                "Error occurred in " + ex.getClass().getSimpleName(),
                getStackTraceAsString(ex),
                ex.getClass().getName(),
                "handle" + exceptionType.replace(" ", "")
            );
            
            // Güvenlik logu da kaydet
            systemLogService.logSecurity(
                exceptionType + " occurred",
                "Error details: " + ex.getMessage(),
                userId,
                ipAddress,
                userAgent
            );
        } catch (Exception logEx) {
            log.error("Failed to log error: {}", logEx.getMessage(), logEx);
        }
    }

    private void logWarn(String exceptionType, Exception ex, WebRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            
            systemLogService.logWarn(
                exceptionType + ": " + ex.getMessage(),
                "Exception occurred in " + ex.getClass().getSimpleName(),
                ex.getClass().getName(),
                "handle" + exceptionType.replace(" ", "")
            );
            
            // İş logu da kaydet
            systemLogService.logBusiness(
                exceptionType + " warning",
                "Warning details: " + ex.getMessage(),
                userId
            );
        } catch (Exception logEx) {
            log.error("Failed to log exception: {}", logEx.getMessage(), logEx);
        }
    }

    private void logSecurity(String exceptionType, Exception ex, WebRequest request) {
        try {
            String userId = getUserIdFromRequest(request);
            String ipAddress = getIpAddressFromRequest(request);
            String userAgent = getUserAgentFromRequest(request);
            
            systemLogService.logSecurity(
                exceptionType + ": " + ex.getMessage(),
                "Security exception occurred in " + ex.getClass().getSimpleName(),
                userId,
                ipAddress,
                userAgent
            );
            
            // Denetim logu da kaydet
            systemLogService.logAudit(
                exceptionType + " security event",
                "Security event details: " + ex.getMessage(),
                userId,
                ipAddress
            );
        } catch (Exception logEx) {
            log.error("Failed to log security exception: {}", logEx.getMessage(), logEx);
        }
    }

    private String getUserIdFromRequest(WebRequest request) {
        // Authorization header'dan user ID'yi çıkarmaya çalış
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // JWT token'dan user ID çıkarma mantığı burada eklenebilir
            // Şimdilik basit bir yaklaşım kullanıyoruz
            return "authenticated_user";
        }
        
        // X-User-ID header'ından da kontrol edebiliriz
        String userId = request.getHeader("X-User-ID");
        if (userId != null && !userId.trim().isEmpty()) {
            return userId;
        }
        
        return "unknown";
    }

    private String getIpAddressFromRequest(WebRequest request) {
        return request.getHeader("X-Forwarded-For") != null ? 
               request.getHeader("X-Forwarded-For") : 
               request.getHeader("X-Real-IP") != null ? 
               request.getHeader("X-Real-IP") : "unknown";
    }

    private String getUserAgentFromRequest(WebRequest request) {
        return request.getHeader("User-Agent") != null ? 
               request.getHeader("User-Agent") : "unknown";
    }

    private String getStackTraceAsString(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString()).append("\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
} 