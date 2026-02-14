package com.example.errortracker.service;

import com.example.errortracker.dto.ErrorLogRequestDto;
import com.example.errortracker.dto.ErrorLogResponseDto;
import com.example.errortracker.model.ErrorLog;
import com.example.errortracker.repository.ErrorLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ErrorLogService
 *
 * This is the Service layer for the Error Tracking System.
 * It sits between the Controller (HTTP layer) and the Repository (database layer).
 *
 * Responsibilities:
 * - Convert DTOs to entities and back
 * - Apply business rules (e.g. severity calculation, timestamp handling)
 * - Interact with the repository for persistence
 *
 * Interview explanation:
 * "I used a Service layer to keep my controllers thin. The controller only handles
 *  HTTP-specific logic and delegates to the service, which contains the core business
 *  logic such as mapping DTOs, calculating severity, and calling the repository."
 */
@Service
public class ErrorLogService {

    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;

    private final ErrorLogRepository errorLogRepository;
    private final AlertService alertService;

    public ErrorLogService(ErrorLogRepository errorLogRepository, AlertService alertService) {
        this.errorLogRepository = errorLogRepository;
        this.alertService = alertService;
    }

    /**
     * Log a new error based on the incoming DTO.
     *
     * Steps:
     * 1. Convert DTO to entity
     * 2. Handle timestamp (client-provided or server time)
     * 3. Save entity using repository
     * 4. Process for alerting
     * 5. Convert saved entity back to response DTO
     */
    @Transactional
    public ErrorLogResponseDto logError(ErrorLogRequestDto requestDto) {
        ErrorLog entity = new ErrorLog();
        entity.setApplicationName(requestDto.getApplicationName());
        entity.setApiName(requestDto.getApiName());
        entity.setStatusCode(requestDto.getStatusCode());
        entity.setMessage(requestDto.getMessage());

        // Handle timestamp from client (optional)
        LocalDateTime timestamp = parseClientTimestampOrNow(requestDto.getTimestamp());
        entity.setTimestamp(timestamp);

        // Severity is recalculated in setStatusCode (inside entity)
        entity.setStatusCode(requestDto.getStatusCode());

        // occurrenceCount starts at 1 by default

        ErrorLog saved = errorLogRepository.save(entity);
        
        // Process the error for alerting (async - don't block the response)
        try {
            alertService.processError(saved);
        } catch (Exception e) {
            // Log error but don't fail the request
            System.err.println("Alert processing failed for error ID: " + saved.getId());
            e.printStackTrace();
        }
        
        return mapToResponseDto(saved);
    }

    /**
     * Get all errors with optional filters.
     *
     * For simplicity, we:
     * - Fetch all errors
     * - Filter in memory using Java Streams
     *
     * For large datasets, we could push filters down to the database
     * using repository methods or custom queries.
     */
    @Transactional(readOnly = true)
    public List<ErrorLogResponseDto> getErrors(
            Integer statusCode,
            String severity,
            String applicationName
    ) {
        List<ErrorLog> all = errorLogRepository.findRecentErrors();

        return all.stream()
                .filter(e -> statusCode == null || statusCode.equals(e.getStatusCode()))
                .filter(e -> severity == null || severity.equalsIgnoreCase(e.getSeverity()))
                .filter(e -> applicationName == null || applicationName.equalsIgnoreCase(e.getApplicationName()))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ======== Helper Methods ========

    private LocalDateTime parseClientTimestampOrNow(String timestamp) {
        if (timestamp == null || timestamp.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(timestamp, ISO_DATE_TIME);
        } catch (DateTimeParseException ex) {
            // If the client sends invalid format, fall back to server time
            return LocalDateTime.now();
        }
    }

    private ErrorLogResponseDto mapToResponseDto(ErrorLog entity) {
        ErrorLogResponseDto dto = new ErrorLogResponseDto();
        dto.setId(entity.getId());
        dto.setApplicationName(entity.getApplicationName());
        dto.setApiName(entity.getApiName());
        dto.setStatusCode(entity.getStatusCode());
        dto.setMessage(entity.getMessage());
        dto.setSeverity(entity.getSeverity());
        dto.setTimestamp(entity.getTimestamp() != null ? entity.getTimestamp().toString() : null);
        dto.setOccurrenceCount(entity.getOccurrenceCount());
        return dto;
    }
}

