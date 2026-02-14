package com.example.errortracker.dto;

/**
 * ErrorLogResponseDto
 *
 * This class represents the JSON structure that our API
 * returns to the frontend or other client applications.
 *
 * We use a separate response DTO so that:
 * - We don't leak internal entity details
 * - We can shape the API response specifically for UI needs
 * - We can evolve the database schema without breaking clients
 */
public class ErrorLogResponseDto {

    private Long id;
    private String applicationName;
    private String apiName;
    private Integer statusCode;
    private String message;
    private String severity;
    private String timestamp;      // formatted as ISO-8601 string
    private Integer occurrenceCount;

    // ======== Getters and Setters ========

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }
}

