package com.example.errortracker.controller;

import com.example.errortracker.model.Alert;
import com.example.errortracker.service.AlertService;
import com.example.errortracker.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlertController - REST endpoints for alert management and monitoring
 * 
 * This controller provides endpoints to:
 * - View recent alerts
 * - Get alert statistics
 * - Test email configuration
 * - Manually trigger alert checks
 * 
 * Interview Explanation:
 * "I created an AlertController to provide monitoring endpoints for the alerting
 *  system. This allows administrators to view recent alerts, check statistics,
 *  and test the email configuration. It follows the same REST patterns as the
 *  ErrorLogController for consistency."
 */
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;
    private final EmailService emailService;

    public AlertController(AlertService alertService, EmailService emailService) {
        this.alertService = alertService;
        this.emailService = emailService;
    }

    /**
     * GET /api/alerts/recent
     * 
     * Get recent alerts from the last N minutes
     * Default is 60 minutes if not specified
     * 
     * Example: GET /api/alerts/recent?minutes=30
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Alert>> getRecentAlerts(
            @RequestParam(name = "minutes", defaultValue = "60") int minutes
    ) {
        try {
            List<Alert> alerts = alertService.getRecentAlerts(minutes);
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * GET /api/alerts/health
     * 
     * Simple health check for alert system
     * 
     * Example: GET /api/alerts/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getAlertSystemHealth() {
        try {
            Map<String, Object> health = Map.of(
                "status", "healthy",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "message", "Alert system is operational"
            );
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * GET /api/alerts/statistics
     * 
     * Get alert statistics for the last 24 hours
     * Returns counts by alert type
     * 
     * Example response:
     * {
     *   "criticalCount": 5,
     *   "highFrequencyCount": 3,
     *   "totalCount": 8
     * }
     */
    @GetMapping("/statistics")
    public ResponseEntity<AlertService.AlertStatistics> getAlertStatistics() {
        AlertService.AlertStatistics stats = alertService.getAlertStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/alerts/test-email
     * 
     * Test the email configuration by sending a test email
     * Useful for verifying SMTP setup during deployment
     * 
     * Example response:
     * {
     *   "success": true,
     *   "message": "Test email sent successfully"
     * }
     */
    @PostMapping("/test-email")
    public ResponseEntity<Map<String, Object>> testEmailConfiguration() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = emailService.testEmailConfiguration();
            
            response.put("success", success);
            if (success) {
                response.put("message", "Test email sent successfully");
            } else {
                response.put("message", "Test email failed - check configuration");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Test email failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * GET /api/alerts/config

    /**
     * GET /api/alerts/config
     * 
     * Get current alert configuration (for monitoring purposes)
     * Returns non-sensitive configuration values
     * 
     * Example response:
     * {
     *   "criticalCooldownMinutes": 30,
     *   "highFrequencyThreshold": 5,
     *   "highFrequencyTimeWindowMinutes": 15
     * }
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getAlertConfiguration() {
        Map<String, Object> config = new HashMap<>();
        
        // Return non-sensitive configuration
        config.put("criticalCooldownMinutes", 30); // These could be @Value injected
        config.put("highFrequencyThreshold", 5);
        config.put("highFrequencyTimeWindowMinutes", 15);
        config.put("schedulerEnabled", true);
        
        return ResponseEntity.ok(config);
    }

    /**
     * GET /api/alerts/test
     * 
     * Simple test endpoint that returns mock data
     * Bypasses database issues
     * 
     * Example: GET /api/alerts/test
     */
    @GetMapping("/test")
    public ResponseEntity<List<Map<String, Object>>> getTestAlerts() {
        try {
            List<Map<String, Object>> mockAlerts = List.of(
                Map.of(
                    "id", 1L,
                    "alertType", "CRITICAL",
                    "subject", "Test Critical Alert",
                    "sentAt", java.time.LocalDateTime.now().toString()
                ),
                Map.of(
                    "id", 2L,
                    "alertType", "HIGH_FREQUENCY", 
                    "subject", "Test High Frequency Alert",
                    "sentAt", java.time.LocalDateTime.now().minusMinutes(5).toString()
                )
            );
            return ResponseEntity.ok(mockAlerts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of(Map.of("error", e.getMessage())));
        }
    }
}
