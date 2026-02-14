package com.example.errortracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot Application Class
 * 
 * This is the entry point of our Error Tracking & Alerting System.
 * 
 * @SpringBootApplication annotation does three things:
 * 1. @Configuration - Marks this as a configuration class
 * 2. @EnableAutoConfiguration - Enables Spring Boot auto-configuration
 * 3. @ComponentScan - Scans for Spring components (controllers, services, etc.)
 * 
 * When you run this class, Spring Boot will:
 * - Start an embedded Tomcat server on port 8080
 * - Initialize SQLite database connection
 * - Scan and load all your controllers, services, repositories
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.example.errortracker")
@EnableJpaRepositories(basePackages = "com.example.errortracker.repository")
@EntityScan(basePackages = "com.example.errortracker.model")
public class ErrorTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErrorTrackerApplication.class, args);
        System.out.println("✅ Error Tracking System is running on http://localhost:8086");
    }
}
