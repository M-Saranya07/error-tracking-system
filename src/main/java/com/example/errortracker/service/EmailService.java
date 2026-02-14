package com.example.errortracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * EmailService - Handles email notifications for alerts
 * 
 * This service is responsible for sending email alerts when critical errors
 * occur. It supports both real email sending and mock mode for development.
 * 
 * Interview Explanation:
 * "I implemented an EmailService that can send alerts via SMTP. For development,
 *  I included a mock mode that logs emails to console instead of actually sending
 *  them. The service uses Spring's JavaMailSender and can be configured through
 *  application properties."
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    
    // Configuration properties from application.properties
    @Value("${alert.email.mock-mode:true}")
    private boolean mockMode;
    
    @Value("${alert.email.from:alerts@errortracker.com}")
    private String fromEmail;
    
    @Value("${alert.email.to:admin@company.com}")
    private String defaultRecipient;
    
    public EmailService() {
        this.mailSender = createMailSender();
    }
    
    /**
     * Send an alert email
     * 
     * @param to Recipient email address
     * @param subject Email subject
     * @param message Email body
     * @return true if sent successfully, false otherwise
     */
    public boolean sendAlertEmail(String to, String subject, String message) {
        try {
            if (mockMode) {
                return sendMockEmail(to, subject, message);
            } else {
                return sendRealEmail(to, subject, message);
            }
        } catch (Exception e) {
            logger.error("Failed to send alert email to: {}", to, e);
            return false;
        }
    }
    
    /**
     * Send email using mock mode (logs to console)
     * Useful for development and testing when SMTP is not configured
     */
    private boolean sendMockEmail(String to, String subject, String message) {
        logger.info("📧 MOCK EMAIL - Would send to: {}", to);
        logger.info("📧 MOCK EMAIL - Subject: {}", subject);
        logger.info("📧 MOCK EMAIL - Message: {}", message);
        logger.info("📧 MOCK EMAIL - From: {}", fromEmail);
        logger.info("--- End of Mock Email ---");
        return true;
    }
    
    /**
     * Send real email using SMTP
     * This requires proper SMTP configuration in application.properties
     */
    private boolean sendRealEmail(String to, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        
        mailSender.send(mailMessage);
        
        logger.info("✅ Alert email sent successfully to: {}", to);
        return true;
    }
    
    /**
     * Create and configure JavaMailSender
     * Uses configuration from application.properties
     */
    private JavaMailSender createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        // These will be overridden by Spring's auto-configuration
        // if proper SMTP settings are provided in application.properties
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("your-email@gmail.com");
        mailSender.setPassword("your-app-password");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        
        return mailSender;
    }
    
    /**
     * Send alert to default recipient
     * Convenience method for sending to the configured admin email
     */
    public boolean sendAlertToDefault(String subject, String message) {
        return sendAlertEmail(defaultRecipient, subject, message);
    }
    
    /**
     * Get the default recipient email address
     * Useful for other services to know where alerts are sent
     */
    public String getDefaultRecipient() {
        return defaultRecipient;
    }
    
    /**
     * Test email configuration
     * Useful for verifying email setup during deployment
     */
    public boolean testEmailConfiguration() {
        String testSubject = "🧪 Error Tracker - Email Configuration Test";
        String testMessage = "This is a test email to verify the email configuration is working correctly.";
        
        boolean result = sendAlertToDefault(testSubject, testMessage);
        
        if (result) {
            logger.info("✅ Email configuration test passed");
        } else {
            logger.error("❌ Email configuration test failed");
        }
        
        return result;
    }
}
