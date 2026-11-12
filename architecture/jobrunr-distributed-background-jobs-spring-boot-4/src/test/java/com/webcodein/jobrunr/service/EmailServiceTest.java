package com.webcodein.jobrunr.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailServiceTest {

    private final EmailService emailService = new EmailService();

    @Test
    @DisplayName("Should successfully execute sendWelcomeEmail without throwing exceptions")
    void testSendWelcomeEmailSuccess() {
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail("user@example.com"));
    }

    @Test
    @DisplayName("Should successfully execute generateMonthlyReport without throwing exceptions")
    void testGenerateMonthlyReportSuccess() {
        assertDoesNotThrow(() -> emailService.generateMonthlyReport());
    }
}
