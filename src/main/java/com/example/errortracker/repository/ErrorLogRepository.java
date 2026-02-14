package com.example.errortracker.repository;

import com.example.errortracker.model.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ErrorLogRepository - Data Access Layer
 * 
 * This is a Spring Data JPA Repository interface.
 * 
 * What Spring Data JPA does for you:
 * - You just extend JpaRepository<ErrorLog, Long>
 * - Spring automatically creates a concrete implementation class
 * - You get CRUD operations for FREE: save(), findAll(), findById(), delete(), etc.
 * - You can define custom query methods just by naming them correctly
 * 
 * Example:
 * - findByStatusCode(500) → Spring generates: SELECT * FROM error_logs WHERE status_code = 500
 * - findBySeverityAndTimestampAfter("CRITICAL", date) → Spring generates complex WHERE clause
 * 
 * Interview Explanation:
 * "I used Spring Data JPA repository pattern. By extending JpaRepository, I get all CRUD operations
 *  without writing SQL. I also added custom query methods like findBySeverity and findByStatusCode
 *  that Spring automatically implements based on method names. This follows the Repository pattern
 *  which separates data access logic from business logic."
 */
@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    
    /**
     * Find all errors by status code
     * Spring generates: SELECT * FROM error_logs WHERE status_code = ?
     */
    List<ErrorLog> findByStatusCode(Integer statusCode);
    
    /**
     * Find all errors by severity level
     * Spring generates: SELECT * FROM error_logs WHERE severity = ?
     */
    List<ErrorLog> findBySeverity(String severity);
    
    /**
     * Find all errors by application name
     * Spring generates: SELECT * FROM error_logs WHERE application_name = ?
     */
    List<ErrorLog> findByApplicationName(String applicationName);
    
    /**
     * Find all CRITICAL errors
     * This is a convenience method
     */
    default List<ErrorLog> findCriticalErrors() {
        return findBySeverity("CRITICAL");
    }
    
    /**
     * Find errors that occurred after a specific timestamp
     * Useful for alerting - "find all errors in last 5 minutes"
     */
    List<ErrorLog> findByTimestampAfter(LocalDateTime timestamp);
    
    /**
     * Find errors by severity AND timestamp (for alerting)
     * Spring generates: SELECT * FROM error_logs WHERE severity = ? AND timestamp > ?
     */
    List<ErrorLog> findBySeverityAndTimestampAfter(String severity, LocalDateTime timestamp);
    
    /**
     * Find errors by status code AND timestamp
     * Useful for filtering: "find all 500 errors in last hour"
     */
    List<ErrorLog> findByStatusCodeAndTimestampAfter(Integer statusCode, LocalDateTime timestamp);
    
    /**
     * Custom query using @Query annotation
     * This gives you full control over SQL if needed
     * 
     * This query finds errors within a date range
     */
    @Query("SELECT e FROM ErrorLog e WHERE e.timestamp BETWEEN :startDate AND :endDate")
    List<ErrorLog> findErrorsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Count errors by severity
     * Returns number of errors for each severity level
     */
    @Query("SELECT e.severity, COUNT(e) FROM ErrorLog e GROUP BY e.severity")
    List<Object[]> countErrorsBySeverity();
    
    /**
     * Find top N most recent errors
     * Useful for dashboard - show latest errors first
     */
    @Query("SELECT e FROM ErrorLog e ORDER BY e.timestamp DESC")
    List<ErrorLog> findRecentErrors();
}
