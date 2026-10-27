package com.bustracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Real-Time Bus Tracking System - Academic Application
 * Sathyabama Institute of Science and Technology
 * 
 * Main Spring Boot Application entry point.
 */
@SpringBootApplication
@EnableScheduling
public class BusTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusTrackingApplication.class, args);
    }
}
