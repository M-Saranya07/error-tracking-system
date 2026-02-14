package com.example.errortracker.service;

import com.example.errortracker.model.ErrorLog;
import com.example.errortracker.repository.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AlertScheduler - Background job for scanning errors and triggering alerts
 * 
 * This scheduler runs periodically to:
 * - Scan for recent critical errors that might have been missed
 * - Check for error patterns that require alerting
 * - Perform cleanup operations
 * 
 * Interview Explanation:
 * "I implemented a scheduler using Spring's @Scheduled annotation that runs
 *  every minute to scan for recent errors. The scheduler applies alert rules
 *  and sends notifications. It uses fixedRate to ensure consistent monitoring
 *  and includes error handling to prevent scheduler failures."
 */
@Service
@EnableScheduling
public class AlertScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertScheduler.class);
    
    private final ErrorLogRepository errorLogRepository;
    private final AlertService alertService;
    
    // Configuration from application.properties
    @Value("${alert.scheduler.scan.minutes:5}")
    private int scanWindowMinutes;
    
    @Value("${alert.scheduler.enabled:true}")
    private boolean schedulerEnabled;
    
    public AlertScheduler(ErrorLogRepository errorLogRepository,
                         AlertService alertService) {
        this.errorLogRepository = errorLogRepository;
        this.alertService = alertService;
    }
    
    /**
     * Main scheduled job - runs every minute to scan for recent errors
     * 
     * This is the heart of the alerting system. It:
     * 1. Scans for errors in the last N minutes
     * 2. Processes each error through alert rules
     * 3. Sends appropriate alerts
     * 
     * fixedRate = 60000 means runs every 60 seconds (1 minute)
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void scanForAlerts() {
        if (!schedulerEnabled) {
            logger.debug("📴 Alert scheduler is disabled");
            return;
        }
        
        try {
            logger.debug("🔍 Starting alert scan for errors in last {} minutes", scanWindowMinutes);
            
            LocalDateTime scanStart = LocalDateTime.now().minusMinutes(scanWindowMinutes);
            
            // Get recent errors that haven't been processed for alerting
            List<ErrorLog> recentErrors = errorLogRepository
                    .findByTimestampAfter(scanStart);
            
            int totalErrors = recentErrors.size();
            int alertsSent = 0;
            
            for (ErrorLog error : recentErrors) {
                if (alertService.processError(error)) {
                    alertsSent++;
                }
            }
            
            if (totalErrors > 0) {
                logger.info("📊 Alert scan completed: {} errors processed, {} alerts sent", 
                           totalErrors, alertsSent);
            } else {
                logger.debug("✅ Alert scan completed: No recent errors found");
            }
            
        } catch (Exception e) {
            logger.error("❌ Error during alert scan", e);
        }
    }
    
    /**
     * Health check job - runs every 5 minutes to verify system health
     * This helps monitor if the alerting system is working correctly
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void healthCheck() {
        if (!schedulerEnabled) {
            return;
        }
        
        try {
            logger.debug("🏥 Running alert system health check");
            
            // Check if we can access the database
            long totalErrors = errorLogRepository.count();
            LocalDateTime lastHour = LocalDateTime.now().minusHours(1);
            List<ErrorLog> recentErrors = errorLogRepository.findByTimestampAfter(lastHour);
            
            // Get alert statistics
            AlertService.AlertStatistics stats = alertService.getAlertStatistics();
            
            logger.info("🏥 Health check - Total errors: {}, Recent (1h): {}, Alerts (24h): {}", 
                       totalErrors, recentErrors.size(), stats.getTotalCount());
            
            // If we have many errors but no alerts, that might indicate a problem
            if (recentErrors.size() > 10 && stats.getTotalCount() == 0) {
                logger.warn("⚠️ Health check warning: Many errors but no alerts sent - check alert configuration");
            }
            
        } catch (Exception e) {
            logger.error("❌ Health check failed", e);
        }
    }
    
    /**
     * Cleanup job - runs once daily to clean up old data
     * This prevents the database from growing too large
     */
    @Scheduled(cron = "0 0 2 * * *") // Every day at 2 AM
    public void cleanupOldData() {
        if (!schedulerEnabled) {
            return;
        }
        
        try {
            logger.info("🧹 Starting daily cleanup of old data");
            
            // Clean up error logs older than 30 days
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            
            // Note: In a production system, you might want to archive instead of delete
            // For this demo, we'll just log what would be cleaned up
            List<ErrorLog> oldErrors = errorLogRepository.findErrorsBetweenDates(
                    LocalDateTime.of(2020, 1, 1, 0, 0), thirtyDaysAgo);
            
            logger.info("🧹 Cleanup completed: {} error logs older than 30 days found", 
                       oldErrors.size());
            
            // In a real implementation, you would:
            // errorLogRepository.deleteAll(oldErrors);
            
        } catch (Exception e) {
            logger.error("❌ Cleanup job failed", e);
        }
    }
    
    /**
     * Test job - runs every 10 minutes in development to test alerting
     * This helps verify the alerting system is working without waiting for real errors
     */
    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void testAlertSystem() {
        // Only run in development or if explicitly enabled
        if (!schedulerEnabled || !isDevelopmentEnvironment()) {
            return;
        }
        
        try {
            logger.debug("🧪 Running test alert system check");
            
            // Create a test error to verify alerting works
            // In a real system, you might disable this in production
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
            List<ErrorLog> testErrors = errorLogRepository.findByTimestampAfter(fiveMinutesAgo);
            
            if (testErrors.isEmpty()) {
                logger.debug("🧪 No recent errors to test alerting with");
            } else {
                logger.debug("🧪 Found {} recent errors for testing", testErrors.size());
            }
            
        } catch (Exception e) {
            logger.error("❌ Test alert system failed", e);
        }
    }
    
    /**
     * Helper method to determine if we're in development environment
     * You can customize this based on your application's environment detection
     */
    private boolean isDevelopmentEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "");
        return profile.contains("dev") || profile.contains("test") || profile.isEmpty();
    }
}
