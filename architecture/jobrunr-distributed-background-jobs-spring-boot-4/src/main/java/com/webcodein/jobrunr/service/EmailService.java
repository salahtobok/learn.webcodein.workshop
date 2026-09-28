package com.webcodein.jobrunr.service;

import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Job(name = "Send welcome email to %0")
    public void sendWelcomeEmail(String email) {
        log.info("Sending welcome email to {}...", email);
        try {
            // Simulate heavy lifting using Virtual Threads
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Welcome email sent to {}", email);
    }

    @Job(name = "Generate monthly report", retries = 2)
    public void generateMonthlyReport() {
        log.info("Generating monthly report...");
        // Simulate heavy reporting
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("Monthly report generated successfully.");
    }
}
