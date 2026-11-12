package com.webcodein.jobrunr.controller;

import com.webcodein.jobrunr.service.EmailService;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobScheduler jobScheduler;
    private final EmailService emailService;

    public JobController(JobScheduler jobScheduler, EmailService emailService) {
        this.jobScheduler = jobScheduler;
        this.emailService = emailService;
    }

    @PostMapping("/enqueue")
    public ResponseEntity<String> enqueueJob(@RequestParam(required = false) String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return ResponseEntity.badRequest().body("Valid email parameter is required");
        }
        // Fire-and-forget job
        jobScheduler.enqueue(() -> emailService.sendWelcomeEmail(email));
        return ResponseEntity.ok("Job enqueued successfully");
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleJob() {
        // Delayed job scheduled 1 minute into the future
        jobScheduler.schedule(Instant.now().plus(Duration.ofMinutes(1)), () -> emailService.generateMonthlyReport());
        return ResponseEntity.ok("Report scheduled in 1 minute");
    }

    @PostMapping("/recurring")
    public ResponseEntity<String> scheduleRecurringJob() {
        // Recurring job triggered daily at midnight
        jobScheduler.scheduleRecurrently("monthly-report-job", "0 0 1 * *", () -> emailService.generateMonthlyReport());
        return ResponseEntity.ok("Recurring report job registered successfully");
    }
}
