package com.example.errortracker.repository;

import com.example.errortracker.model.Alert;
import com.example.errortracker.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AlertRepository - Data Access Layer for Alerts
 * 
 * This repository handles all database operations for the Alert entity.
 * It includes methods to check for recent alerts (to prevent spamming)
 * and retrieve alert history.
 * 
 * Interview Explanation:
 * "I created an AlertRepository to manage alert persistence. The repository
 *  includes methods to check if an alert was recently sent for the same error,
 *  which prevents alert spamming. I also added queries to find alerts by type
 *  and time ranges for reporting purposes."
 */
@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    /**
     * Find alerts for a specific error log
     * Useful to see all notifications sent for a particular error
     */
    List<Alert> findByErrorLog(ErrorLog errorLog);
    
    /**
     * Find alerts by alert type
     * Useful for reporting on different types of alerts
     */
    List<Alert> findByAlertType(String alertType);
    
    /**
     * Find alerts sent after a specific timestamp
     * Useful for cleanup and reporting
     */
    List<Alert> findBySentAtAfter(LocalDateTime timestamp);
    
    /**
     * Check if an alert was recently sent for the same error and type
     * This is crucial for preventing alert spamming
     * 
     * @param errorLog The error to check
     * @param alertType The type of alert
     * @param sinceTime Check alerts sent after this time
     * @return Number of recent alerts
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.errorLog = :errorLog " +
           "AND a.alertType = :alertType AND a.sentAt > :sinceTime")
    long countRecentAlertsForError(
            @Param("errorLog") ErrorLog errorLog,
            @Param("alertType") String alertType,
            @Param("sinceTime") LocalDateTime sinceTime
    );
    
    /**
     * Find all alerts sent in the last N minutes
     * Useful for monitoring alert frequency
     */
    @Query("SELECT a FROM Alert a WHERE a.sentAt > :sinceTime ORDER BY a.sentAt DESC")
    List<Alert> findRecentAlerts(@Param("sinceTime") LocalDateTime sinceTime);
    
    /**
     * Count alerts by type in a time range
     * Useful for analytics and reporting
     */
    @Query("SELECT a.alertType, COUNT(a) FROM Alert a " +
           "WHERE a.sentAt BETWEEN :startDate AND :endDate " +
           "GROUP BY a.alertType")
    List<Object[]> countAlertsByTypeBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find alerts with specific status
     * Useful for retrying failed alerts
     */
    List<Alert> findByStatus(String status);
    
    /**
     * Delete old alerts (cleanup operation)
     * Useful for maintaining database size
     */
    void deleteBySentAtBefore(LocalDateTime cutoffDate);
}
