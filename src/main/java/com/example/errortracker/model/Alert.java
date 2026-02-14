package com.example.errortracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Alert Entity - Represents an alert notification sent to administrators
 * 
 * This entity tracks all alerts that have been sent out by the system.
 * It helps prevent duplicate alerts and maintains alert history.
 * 
 * Interview Explanation:
 * "I created an Alert entity to track all notifications sent by the system.
 *  This prevents alert spamming and provides audit trails. Each alert is
 *  linked to an ErrorLog and includes metadata like alert type and timestamp."
 */
@Entity
@Table(name = "alerts")
public class Alert {
    
    /**
     * Primary Key - Auto-generated ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Reference to the error that triggered this alert
     * Many alerts can point to the same error (e.g., repeated notifications)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_log_id", nullable = false)
    private ErrorLog errorLog;
    
    /**
     * Type of alert (CRITICAL, HIGH_FREQUENCY, THRESHOLD_EXCEEDED)
     * This helps categorize why the alert was triggered
     */
    @Column(nullable = false, length = 50)
    private String alertType;
    
    /**
     * Email address where this alert was sent
     * Useful for tracking which recipients received which alerts
     */
    @Column(nullable = false, length = 255)
    private String recipientEmail;
    
    /**
     * Subject line of the alert email
     * Stored for audit purposes
     */
    @Column(nullable = false, length = 255)
    private String subject;
    
    /**
     * Alert message content
     * Stored in TEXT format to accommodate long messages
     */
    @Column(columnDefinition = "TEXT")
    private String message;
    
    /**
     * When this alert was sent
     * Used for cooldown periods and alert history
     */
    @Column(nullable = false)
    private LocalDateTime sentAt;
    
    /**
     * Alert status (SENT, FAILED, PENDING)
     * Helps track delivery status
     */
    @Column(nullable = false, length = 20)
    private String status = "SENT";
    
    // Default constructor (required by JPA)
    public Alert() {
        this.sentAt = LocalDateTime.now();
    }
    
    // Constructor with main fields
    public Alert(ErrorLog errorLog, String alertType, String recipientEmail, 
                String subject, String message) {
        this();
        this.errorLog = errorLog;
        this.alertType = alertType;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
    }
    
    // ========== Getters and Setters ==========
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ErrorLog getErrorLog() {
        return errorLog;
    }
    
    public void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }
    
    public String getAlertType() {
        return alertType;
    }
    
    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", alertType='" + alertType + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", subject='" + subject + '\'' +
                ", sentAt=" + sentAt +
                ", status='" + status + '\'' +
                '}';
    }
}
