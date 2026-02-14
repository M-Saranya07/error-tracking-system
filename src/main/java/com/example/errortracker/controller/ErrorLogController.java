package com.example.errortracker.controller;

import com.example.errortracker.dto.ErrorLogRequestDto;
import com.example.errortracker.dto.ErrorLogResponseDto;
import com.example.errortracker.service.ErrorLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ErrorLogController
 *
 * This is the REST API layer of the application.
 * It exposes endpoints for:
 * - Logging errors (POST /api/errors)
 * - Fetching errors (GET /api/errors)
 *
 * Responsibilities:
 * - Handle HTTP details (URLs, methods, status codes)
 * - Validate incoming requests using @Valid
 * - Delegate business logic to ErrorLogService
 *
 * Interview explanation:
 * "My controller layer is very thin. It maps HTTP requests to
 *  service calls and returns DTOs. All business logic, such as
 *  severity calculation and repository calls, is handled in the
 *  service layer so that the controller remains simple."
 */
@RestController
@RequestMapping("/api/errors")
@CrossOrigin(origins = "*")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    /**
     * POST /api/errors
     *
     * This endpoint is used by client applications to log an error.
     *
     * Example JSON body:
     * {
     *   "applicationName": "payment-service",
     *   "apiName": "/api/payments",
     *   "statusCode": 500,
     *   "message": "Database connection failed"
     * }
     */
    @PostMapping
    public ResponseEntity<ErrorLogResponseDto> logError(
            @Valid @RequestBody ErrorLogRequestDto requestDto
    ) {
        ErrorLogResponseDto responseDto = errorLogService.logError(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * GET /api/errors
     *
     * This endpoint returns a list of error logs.
     * It supports optional filters as query parameters.
     *
     * Example:
     * - GET /api/errors
     * - GET /api/errors?statusCode=500
     * - GET /api/errors?severity=CRITICAL&applicationName=payment-service
     */
    @GetMapping
    public ResponseEntity<List<ErrorLogResponseDto>> getErrors(
            @RequestParam(name = "statusCode", required = false) Integer statusCode,
            @RequestParam(name = "severity", required = false) String severity,
            @RequestParam(name = "applicationName", required = false) String applicationName
    ) {
        List<ErrorLogResponseDto> errors =
                errorLogService.getErrors(statusCode, severity, applicationName);
        return ResponseEntity.ok(errors);
    }
}

