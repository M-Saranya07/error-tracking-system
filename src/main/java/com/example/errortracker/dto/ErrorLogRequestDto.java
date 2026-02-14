package com.example.errortracker.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ErrorLogRequestDto
 *
 * This class represents the JSON body that client applications
 * send to our API when they want to log an error.
 *
 * We keep this separate from the JPA entity so that:
 * - We can validate input using annotations
 * - We control exactly what fields external clients can send
 * - Our database model (entity) can change without breaking the API
 *
 * Interview explanation:
 * "I created request/response DTOs so that I don't expose the JPA
 *  entity directly. The controller receives a DTO, the service layer
 *  maps it to the entity, and the repository persists it."
 */
public class ErrorLogRequestDto {

    @NotBlank(message = "Application name is required")
    private String applicationName;

    @NotBlank(message = "API name is required")
    private String apiName;

    @NotNull(message = "Status code is required")
    @Min(value = 100, message = "Status code must be >= 100")
    @Max(value = 599, message = "Status code must be <= 599")
    private Integer statusCode;

    // Optional error message or stack trace snippet
    private String message;

    /**
     * Optional timestamp string from the client.
     * Format: ISO-8601, e.g. "2026-02-04T10:30:00"
     *
     * If this is null or invalid, we will use the server time
     * inside the service layer.
     */
    private String timestamp;

    // ======== Getters and Setters ========

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

