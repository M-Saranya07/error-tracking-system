package com.example.errortracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ErrorLog Entity - Represents an error log entry in the database
 * 
 * This is a JPA Entity class. JPA (Java Persistence API) is an ORM (Object-Relational Mapping) framework.
 * 
 * What this means:
 * - This Java class represents a table in SQLite database
 * - Each field in this class becomes a column in the "error_logs" table
 * - When you save an ErrorLog object, JPA automatically converts it to SQL INSERT
 * - When you query, JPA converts SQL results back to ErrorLog objects
 * 
 * Interview Explanation:
 * "I created an ErrorLog entity using JPA annotations. This entity maps to an error_logs table 
 *  in SQLite, with fields like applicationName, apiName, statusCode, severity, and timestamp. 
 *  JPA handles all the SQL operations automatically, so I don't write raw SQL queries."
 */
@Entity
@Table(name = "error_logs")
public class ErrorLog {
    
    /**
     * Primary Key - Auto-generated ID
     * @GeneratedValue(strategy = GenerationType.IDENTITY) means SQLite will auto-increment this
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Name of the application that reported this error
     * Example: "payment-service", "user-service", "api-gateway"
     */
    @Column(nullable = false, length = 100)
    private String applicationName;
    
    /**
     * API endpoint or module name where error occurred
     * Example: "/api/payments", "/api/users/login", "PaymentService.processPayment"
     */
    @Column(nullable = false, length = 200)
    private String apiName;
    
    /**
     * HTTP Status Code (4xx = client error, 5xx = server error)
     * Examples: 400, 404, 500, 503
     */
    @Column(nullable = false)
    private Integer statusCode;
    
    /**
     * Error message or exception details
     * Example: "NullPointerException at line 45", "Database connection failed"
     */
    @Column(columnDefinition = "TEXT")
    private String message;
    
    /**
     * Severity level - automatically calculated based on statusCode
     * Values: INFO, WARNING, ERROR, CRITICAL
     * - 4xx → WARNING
     * - 5xx → CRITICAL
     */
    @Column(nullable = false, length = 20)
    private String severity;
    
    /**
     * Timestamp when error occurred
     * LocalDateTime stores both date and time
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    /**
     * Number of times this same error occurred (for aggregation)
     * Useful for tracking repeated errors
     */
    @Column(nullable = false)
    private Integer occurrenceCount = 1;
    
    // Default constructor (required by JPA)
    public ErrorLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Constructor with main fields
    public ErrorLog(String applicationName, String apiName, Integer statusCode, String message) {
        this();
        this.applicationName = applicationName;
        this.apiName = apiName;
        this.statusCode = statusCode;
        this.message = message;
        this.severity = calculateSeverity(statusCode);
    }
    
    /**
     * Helper method to calculate severity based on status code
     * This is business logic - we'll move this to Service layer later
     */
    private String calculateSeverity(Integer statusCode) {
        if (statusCode >= 500) {
            return "CRITICAL";
        } else if (statusCode >= 400) {
            return "WARNING";
        } else {
            return "INFO";
        }
    }
    
    // ========== Getters and Setters ==========
    // JPA needs getters/setters to read/write data
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    public String getApiName() {
        return apiName;
    }
    
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        // Recalculate severity when status code changes
        this.severity = calculateSeverity(statusCode);
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }
    
    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }
    
    @Override
    public String toString() {
        return "ErrorLog{" +
                "id=" + id +
                ", applicationName='" + applicationName + '\'' +
                ", apiName='" + apiName + '\'' +
                ", statusCode=" + statusCode +
                ", severity='" + severity + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
