package com.example.errortracker.service;

import com.example.errortracker.model.Alert;
import com.example.errortracker.model.ErrorLog;
import com.example.errortracker.repository.AlertRepository;
import com.example.errortracker.repository.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AlertService - Business logic for error alerting
 * 
 * This service contains the core alerting logic:
 * - Determines when to send alerts based on error severity and frequency
 * - Prevents alert spamming through cooldown periods
 * - Generates appropriate alert messages
 * 
 * Interview Explanation:
 * "I implemented an AlertService that contains the business logic for determining
 *  when to send alerts. It checks error severity, frequency, and applies cooldown
 *  periods to prevent alert fatigue. The service uses different alert types like
 *  CRITICAL and HIGH_FREQUENCY with configurable thresholds."
 */
@Service
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    private final ErrorLogRepository errorLogRepository;
    private final AlertRepository alertRepository;
    private final EmailService emailService;
    
    // Alert configuration from application.properties
    @Value("${alert.cooldown.minutes.critical:30}")
    private int criticalCooldownMinutes;
    
    @Value("${alert.cooldown.minutes.high-frequency:60}")
    private int highFrequencyCooldownMinutes;
    
    @Value("${alert.threshold.high-frequency.count:5}")
    private int highFrequencyThreshold;
    
    @Value("${alert.threshold.high-frequency.minutes:15}")
    private int highFrequencyTimeWindowMinutes;
    
    public AlertService(ErrorLogRepository errorLogRepository,
                       AlertRepository alertRepository,
                       EmailService emailService) {
        this.errorLogRepository = errorLogRepository;
        this.alertRepository = alertRepository;
        this.emailService = emailService;
    }
    
    /**
     * Process a new error and determine if an alert should be sent
     * This is the main entry point for alerting logic
     * 
     * @param errorLog The error that was just logged
     * @return true if an alert was sent, false otherwise
     */
    @Transactional
    public boolean processError(ErrorLog errorLog) {
        logger.info("🔍 Processing error for alerting: {} - {}", 
                   errorLog.getApplicationName(), errorLog.getApiName());
        
        boolean alertSent = false;
        
        // Check for critical errors (status >= 500)
        if (shouldSendCriticalAlert(errorLog)) {
            alertSent = sendCriticalAlert(errorLog) || alertSent;
        }
        
        // Check for high frequency errors
        if (shouldSendHighFrequencyAlert(errorLog)) {
            alertSent = sendHighFrequencyAlert(errorLog) || alertSent;
        }
        
        if (alertSent) {
            logger.info("🚨 Alert(s) sent for error: {}", errorLog.getId());
        } else {
            logger.debug("ℹ️ No alerts sent for error: {} (within cooldown or threshold not met)", 
                        errorLog.getId());
        }
        
        return alertSent;
    }
    
    /**
     * Check if a critical alert should be sent
     * Critical alerts are sent for 5xx errors with cooldown period
     */
    private boolean shouldSendCriticalAlert(ErrorLog errorLog) {
        if (errorLog.getStatusCode() >= 500) {
            LocalDateTime cooldownStart = LocalDateTime.now()
                    .minusMinutes(criticalCooldownMinutes);
            
            long recentAlerts = alertRepository.countRecentAlertsForError(
                    errorLog, "CRITICAL", cooldownStart);
            
            return recentAlerts == 0;
        }
        return false;
    }
    
    /**
     * Check if a high frequency alert should be sent
     * High frequency alerts are sent when many errors occur in a short time
     */
    private boolean shouldSendHighFrequencyAlert(ErrorLog errorLog) {
        LocalDateTime timeWindowStart = LocalDateTime.now()
                .minusMinutes(highFrequencyTimeWindowMinutes);
        
        // Count similar errors in the time window
        List<ErrorLog> recentSimilarErrors = errorLogRepository
                .findByStatusCodeAndTimestampAfter(
                        errorLog.getStatusCode(), timeWindowStart)
                .stream()
                .filter(e -> e.getApplicationName().equals(errorLog.getApplicationName()) &&
                           e.getApiName().equals(errorLog.getApiName()))
                .toList();
        
        // Check if we've crossed the threshold
        if (recentSimilarErrors.size() >= highFrequencyThreshold) {
            // Check if we haven't sent a high frequency alert recently
            LocalDateTime cooldownStart = LocalDateTime.now()
                    .minusMinutes(highFrequencyCooldownMinutes);
            
            long recentAlerts = alertRepository.countRecentAlertsForError(
                    errorLog, "HIGH_FREQUENCY", cooldownStart);
            
            return recentAlerts == 0;
        }
        
        return false;
    }
    
    /**
     * Send a critical error alert
     */
    private boolean sendCriticalAlert(ErrorLog errorLog) {
        String subject = String.format("🚨 CRITICAL ERROR: %s - %s", 
                                      errorLog.getApplicationName(), 
                                      errorLog.getApiName());
        
        String message = buildCriticalAlertMessage(errorLog);
        
        boolean emailSent = emailService.sendAlertToDefault(subject, message);
        
        if (emailSent) {
            // Record the alert in database
            Alert alert = new Alert(errorLog, "CRITICAL", 
                                  emailService.getDefaultRecipient(), 
                                  subject, message);
            alertRepository.save(alert);
            logger.info("🚨 Critical alert sent for error ID: {}", errorLog.getId());
            return true;
        }
        
        return false;
    }
    
    /**
     * Send a high frequency error alert
     */
    private boolean sendHighFrequencyAlert(ErrorLog errorLog) {
        String subject = String.format("⚠️ HIGH FREQUENCY ERROR: %s - %s", 
                                      errorLog.getApplicationName(), 
                                      errorLog.getApiName());
        
        String message = buildHighFrequencyAlertMessage(errorLog);
        
        boolean emailSent = emailService.sendAlertToDefault(subject, message);
        
        if (emailSent) {
            // Record the alert in database
            Alert alert = new Alert(errorLog, "HIGH_FREQUENCY", 
                                  emailService.getDefaultRecipient(), 
                                  subject, message);
            alertRepository.save(alert);
            logger.info("⚠️ High frequency alert sent for error ID: {}", errorLog.getId());
            return true;
        }
        
        return false;
    }
    
    /**
     * Build alert message for critical errors
     */
    private String buildCriticalAlertMessage(ErrorLog errorLog) {
        return String.format("""
                🚨 CRITICAL ERROR ALERT 🚨
                
                Application: %s
                API: %s
                Status Code: %d
                Severity: %s
                Time: %s
                Message: %s
                
                This is a critical error that requires immediate attention!
                
                Please investigate and resolve this issue as soon as possible.
                """,
                errorLog.getApplicationName(),
                errorLog.getApiName(),
                errorLog.getStatusCode(),
                errorLog.getSeverity(),
                errorLog.getTimestamp(),
                errorLog.getMessage()
        );
    }
    
    /**
     * Build alert message for high frequency errors
     */
    private String buildHighFrequencyAlertMessage(ErrorLog errorLog) {
        LocalDateTime timeWindowStart = LocalDateTime.now()
                .minusMinutes(highFrequencyTimeWindowMinutes);
        
        List<ErrorLog> recentErrors = errorLogRepository
                .findByStatusCodeAndTimestampAfter(
                        errorLog.getStatusCode(), timeWindowStart)
                .stream()
                .filter(e -> e.getApplicationName().equals(errorLog.getApplicationName()) &&
                           e.getApiName().equals(errorLog.getApiName()))
                .toList();
        
        return String.format("""
                ⚠️ HIGH FREQUENCY ERROR ALERT ⚠️
                
                Application: %s
                API: %s
                Status Code: %d
                Time Window: Last %d minutes
                Error Count: %d (threshold: %d)
                
                Most Recent Error:
                Time: %s
                Message: %s
                
                This endpoint is experiencing a high volume of errors.
                Please investigate the underlying cause.
                """,
                errorLog.getApplicationName(),
                errorLog.getApiName(),
                errorLog.getStatusCode(),
                highFrequencyTimeWindowMinutes,
                recentErrors.size(),
                highFrequencyThreshold,
                errorLog.getTimestamp(),
                errorLog.getMessage()
        );
    }
    
    /**
     * Get recent alerts for monitoring
     */
    @Transactional(readOnly = true)
    public List<Alert> getRecentAlerts(int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return alertRepository.findRecentAlerts(since);
    }
    
    /**
     * Get alert statistics
     */
    @Transactional(readOnly = true)
    public AlertStatistics getAlertStatistics() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        
        List<Object[]> stats = alertRepository.countAlertsByTypeBetweenDates(
                last24Hours, LocalDateTime.now());
        
        AlertStatistics statistics = new AlertStatistics();
        
        for (Object[] stat : stats) {
            String alertType = (String) stat[0];
            Long count = (Long) stat[1];
            
            statistics.addAlertCount(alertType, count.intValue());
        }
        
        return statistics;
    }
    
    /**
     * Simple statistics holder
     */
    public static class AlertStatistics {
        private int criticalCount = 0;
        private int highFrequencyCount = 0;
        private int totalCount = 0;
        
        public void addAlertCount(String alertType, int count) {
            totalCount += count;
            switch (alertType) {
                case "CRITICAL" -> criticalCount += count;
                case "HIGH_FREQUENCY" -> highFrequencyCount += count;
            }
        }
        
        public int getCriticalCount() { return criticalCount; }
        public int getHighFrequencyCount() { return highFrequencyCount; }
        public int getTotalCount() { return totalCount; }
    }
}
