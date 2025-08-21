# Email Logging System

## Overview

The Email Logging System provides comprehensive logging for all email operations in the Prospect CRM application. Every email operation is automatically logged to the `email_logs` table, providing detailed tracking and audit capabilities.

## Features

- **Comprehensive Logging**: All email operations are logged with detailed information
- **Success/Failure Tracking**: Both successful and failed operations are logged
- **User Association**: All logs are associated with specific users
- **Error Details**: Failed operations include detailed error messages
- **Timestamp Tracking**: All operations include precise timestamps
- **Draft Association**: Email drafts are linked to their corresponding logs
- **Multiple Operation Types**: Supports various email operations (send, read, delete, list, etc.)

## Database Structure

### EmailLog Entity

```java
@Entity
@Table(name = "email_logs")
public class EmailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draft_id")
    private EmailDraft draftId;

    private String recipientEmail;
    private String status;
    private Boolean responseReceived;
    private String errorMessage;
    private LocalDateTime sentAt;
}
```

## Logged Operations

### 1. Email Sending Operations

#### Direct Email Sending
- **Status**: `SENT`, `FAILED`
- **Logged For**: Each recipient in the email
- **Details**: Recipient email, success/failure status, error message

#### Draft Email Sending
- **Status**: `DRAFT_SENT`, `DRAFT_FAILED`
- **Logged For**: Each recipient in the draft
- **Details**: Recipient email, draft association, success/failure status

#### SMTP Email Sending
- **Status**: Logged via SystemLog (no userId available)
- **Details**: From email, recipients, subject, success/failure status

### 2. Email Reading Operations

#### Email Reading
- **Status**: `READ_SUCCESS`, `READ_FAILED`
- **Details**: Email ID, success/failure status, error message

#### Email Listing
- **Status**: `LIST_SUCCESS`, `LIST_FAILED`
- **Details**: Provider, success/failure status, error message

### 3. Email Management Operations

#### Email Deletion
- **Status**: `DELETE_SUCCESS`, `DELETE_FAILED`
- **Logged For**: Each email ID being deleted
- **Details**: Email ID, success/failure status, error message

#### Email Marking Operations
- **Mark as Read**: `MARK_READ_SUCCESS`, `MARK_READ_FAILED`
- **Mark as Unread**: `MARK_UNREAD_SUCCESS`, `MARK_UNREAD_FAILED`
- **Star Email**: `STAR_SUCCESS`, `STAR_FAILED`
- **Unstar Email**: `UNSTAR_SUCCESS`, `UNSTAR_FAILED`

### 4. Email Template Operations

#### Template Rendering
- **Status**: `TEMPLATE_RENDER_SUCCESS`, `TEMPLATE_RENDER_FAILED`
- **Details**: Template name, variables, success/failure status

## EmailLogService Methods

### Core Logging Methods

```java
// Email sending operations
public void logEmailSent(Long userId, String recipientEmail, String status, String errorMessage)
public void logEmailSent(Long userId, String recipientEmail, String status, String errorMessage, EmailDraft draft)

// Email reading operations
public void logEmailRead(Long userId, String emailAddress, String status, String errorMessage)

// Email deletion operations
public void logEmailDelete(Long userId, String emailAddress, String status, String errorMessage)

// Email listing operations
public void logEmailList(Long userId, String emailAddress, String status, String errorMessage)

// Email action operations (mark as read/unread, star/unstar)
public void logEmailAction(Long userId, String emailId, String action, String status, String errorMessage)

// Email template operations
public void logEmailTemplateRendering(Long userId, String templateName, String status, String errorMessage)
```

### Query Methods

```java
// Get user's email logs
public List<EmailLog> getUserEmailLogs(Long userId)

// Get logs by date range
public List<EmailLog> getEmailLogsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate)

// Get logs by status
public List<EmailLog> getEmailLogsByStatus(Long userId, String status)

// Get failed email logs
public List<EmailLog> getFailedEmailLogs(Long userId)
```

## Integration Points

### EmailService Integration

The `EmailService` automatically logs all email operations:

```java
// Email sending
public void sendEmail(Long userId, EmailSendRequestDto request) {
    try {
        // ... email sending logic ...
        
        // Log successful email send for each recipient
        for (String recipient : request.getToEmails()) {
            emailLogService.logEmailSent(userId, recipient, "SENT", null);
        }
        
    } catch (Exception e) {
        // Log failed email send for each recipient
        for (String recipient : request.getToEmails()) {
            emailLogService.logEmailSent(userId, recipient, "FAILED", e.getMessage());
        }
    }
}
```

### EmailDraftService Integration

The `EmailDraftService` logs draft-related operations:

```java
// Draft sending
public void sendDraft(Long userId, Long draftId) {
    try {
        // ... draft sending logic ...
        
        // Log successful draft sending for each recipient
        List<String> allRecipients = convertStringToList(draft.getToEmails());
        for (String recipient : allRecipients) {
            emailLogService.logEmailSent(userId, recipient, "DRAFT_SENT", null, draft);
        }
        
    } catch (Exception e) {
        // Log failed draft sending for each recipient
        for (String recipient : allRecipients) {
            emailLogService.logEmailSent(userId, recipient, "DRAFT_FAILED", e.getMessage(), draft);
        }
    }
}
```

## Status Codes

### Success Statuses
- `SENT` - Email sent successfully
- `DRAFT_SENT` - Draft email sent successfully
- `READ_SUCCESS` - Email read successfully
- `LIST_SUCCESS` - Email list retrieved successfully
- `DELETE_SUCCESS` - Email deleted successfully
- `MARK_READ_SUCCESS` - Email marked as read successfully
- `MARK_UNREAD_SUCCESS` - Email marked as unread successfully
- `STAR_SUCCESS` - Email starred successfully
- `UNSTAR_SUCCESS` - Email unstarred successfully
- `TEMPLATE_RENDER_SUCCESS` - Template rendered successfully

### Failure Statuses
- `FAILED` - Email sending failed
- `DRAFT_FAILED` - Draft email sending failed
- `READ_FAILED` - Email reading failed
- `LIST_FAILED` - Email listing failed
- `DELETE_FAILED` - Email deletion failed
- `MARK_READ_FAILED` - Mark as read failed
- `MARK_UNREAD_FAILED` - Mark as unread failed
- `STAR_FAILED` - Star operation failed
- `UNSTAR_FAILED` - Unstar operation failed
- `TEMPLATE_RENDER_FAILED` - Template rendering failed

## Usage Examples

### Logging Email Sending

```java
// Successful email send
emailLogService.logEmailSent(userId, "recipient@example.com", "SENT", null);

// Failed email send
emailLogService.logEmailSent(userId, "recipient@example.com", "FAILED", "SMTP connection timeout");
```

### Logging Email Actions

```java
// Mark email as read
emailLogService.logEmailAction(userId, "email123", "MARK_READ", "SUCCESS", null);

// Star email
emailLogService.logEmailAction(userId, "email456", "STAR", "SUCCESS", null);
```

### Querying Email Logs

```java
// Get all email logs for a user
List<EmailLog> userLogs = emailLogService.getUserEmailLogs(userId);

// Get failed email logs
List<EmailLog> failedLogs = emailLogService.getFailedEmailLogs(userId);

// Get logs by date range
LocalDateTime startDate = LocalDateTime.now().minusDays(7);
LocalDateTime endDate = LocalDateTime.now();
List<EmailLog> recentLogs = emailLogService.getEmailLogsByDateRange(userId, startDate, endDate);
```

## Repository Queries

The `EmailLogRepository` provides various query methods:

```java
@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    
    // Get user's email logs ordered by sent date
    List<EmailLog> findByUserIdOrderBySentAtDesc(Users userId);
    
    // Get logs by date range
    List<EmailLog> findByUserIdAndSentAtBetweenOrderBySentAtDesc(Users userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Get logs by status
    List<EmailLog> findByUserIdAndStatusOrderBySentAtDesc(Users userId, String status);
    
    // Get failed logs (status contains FAILED)
    List<EmailLog> findByUserIdAndStatusContainingIgnoreCaseOrderBySentAtDesc(Users userId, String status);
}
```

## Error Handling

All logging operations include comprehensive error handling:

```java
@Transactional
public void logEmailSent(Long userId, String recipientEmail, String status, String errorMessage) {
    try {
        // ... logging logic ...
        
        systemLogService.logInfo("Email log created", 
            "User: " + userId + ", Recipient: " + recipientEmail + ", Status: " + status,
            "EmailLogService", "logEmailSent");
            
    } catch (Exception e) {
        systemLogService.logError("Failed to create email log", e.getMessage(), e.getStackTrace().toString(),
            "EmailLogService", "logEmailSent");
        log.error("Error creating email log: {}", e.getMessage(), e);
    }
}
```

## Performance Considerations

- **Batch Logging**: Multiple recipients are logged individually for detailed tracking
- **Async Logging**: Consider implementing async logging for high-volume operations
- **Indexing**: Ensure proper database indexing on frequently queried fields
- **Cleanup**: Implement periodic cleanup of old log records

## Monitoring and Analytics

The email logging system enables:

- **Email Delivery Tracking**: Monitor success/failure rates
- **User Activity Analysis**: Track email usage patterns
- **Error Analysis**: Identify common failure patterns
- **Performance Monitoring**: Track email operation performance
- **Compliance**: Maintain audit trails for regulatory requirements

## Future Enhancements

- **Real-time Notifications**: Alert users of email failures
- **Analytics Dashboard**: Visual representation of email statistics
- **Export Functionality**: Export email logs for external analysis
- **Advanced Filtering**: More sophisticated query capabilities
- **Integration with External Tools**: Connect with email analytics platforms 