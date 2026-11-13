package com.webcodein.workshop.tls.engine;

import com.webcodein.workshop.tls.model.CipherSuite;
import com.webcodein.workshop.tls.model.EarlyDataPacket;
import com.webcodein.workshop.tls.model.SessionTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EarlyDataSecurityManagerTest {

    private EarlyDataSecurityManager securityManager;
    private SessionTicket activeTicket;
    private byte[] resumptionSecret;

    @BeforeEach
    void setUp() {
        securityManager = new EarlyDataSecurityManager();
        resumptionSecret = "resumption-master-secret-bytes-32".getBytes(StandardCharsets.UTF_8);
        activeTicket = securityManager.issueTicket(
                CipherSuite.TLS_AES_128_GCM_SHA256,
                resumptionSecret,
                Duration.ofHours(24)
        );
    }

    @Test
    @DisplayName("Valid early data packet with safe GET method is accepted")
    void testValidEarlyDataAccepted() {
        Instant now = Instant.now();
        EarlyDataPacket packet = new EarlyDataPacket(
                activeTicket.ticketId(),
                "encrypted-get-request".getBytes(StandardCharsets.UTF_8),
                now,
                "GET",
                "/api/v1/profile"
        );

        EarlyDataSecurityManager.ValidationResult result = securityManager.validateEarlyData(packet, now);

        assertThat(result.isAccepted()).isTrue();
        assertThat(result.status()).isEqualTo(EarlyDataSecurityManager.ValidationStatus.ACCEPTED);
        assertThat(securityManager.isTicketRedeemed(activeTicket.ticketId())).isTrue();
    }

    @Test
    @DisplayName("Replaying the same session ticket is detected and rejected")
    void testReplayAttackDetectedAndRejected() {
        Instant now = Instant.now();
        EarlyDataPacket packet1 = new EarlyDataPacket(
                activeTicket.ticketId(),
                "encrypted-get-request".getBytes(StandardCharsets.UTF_8),
                now,
                "GET",
                "/api/v1/profile"
        );

        // First use is accepted
        EarlyDataSecurityManager.ValidationResult result1 = securityManager.validateEarlyData(packet1, now);
        assertThat(result1.isAccepted()).isTrue();

        // Second use (replay) is rejected
        EarlyDataPacket packet2 = new EarlyDataPacket(
                activeTicket.ticketId(),
                "encrypted-get-request".getBytes(StandardCharsets.UTF_8),
                now.plusSeconds(1),
                "GET",
                "/api/v1/profile"
        );
        EarlyDataSecurityManager.ValidationResult result2 = securityManager.validateEarlyData(packet2, now.plusSeconds(1));

        assertThat(result2.isAccepted()).isFalse();
        assertThat(result2.status()).isEqualTo(EarlyDataSecurityManager.ValidationStatus.REJECTED_REPLAY_DETECTED);
        assertThat(result2.message()).contains("Replay attack detected");
    }

    @Test
    @DisplayName("Non-idempotent HTTP method POST in early data is rejected")
    void testNonIdempotentMethodRejected() {
        Instant now = Instant.now();
        EarlyDataPacket packet = new EarlyDataPacket(
                activeTicket.ticketId(),
                "encrypted-post-transfer".getBytes(StandardCharsets.UTF_8),
                now,
                "POST",
                "/api/v1/transfers"
        );

        EarlyDataSecurityManager.ValidationResult result = securityManager.validateEarlyData(packet, now);

        assertThat(result.isAccepted()).isFalse();
        assertThat(result.status()).isEqualTo(EarlyDataSecurityManager.ValidationStatus.REJECTED_NON_IDEMPOTENT_METHOD);
        assertThat(result.message()).contains("Unsafe HTTP method 'POST'");
    }

    @Test
    @DisplayName("Expired session ticket is rejected")
    void testExpiredTicketRejected() {
        SessionTicket shortLivedTicket = securityManager.issueTicket(
                CipherSuite.TLS_AES_128_GCM_SHA256,
                resumptionSecret,
                Duration.ofSeconds(1)
        );

        Instant future = Instant.now().plusSeconds(10);
        EarlyDataPacket packet = new EarlyDataPacket(
                shortLivedTicket.ticketId(),
                "payload".getBytes(StandardCharsets.UTF_8),
                future,
                "GET",
                "/api/v1/data"
        );

        EarlyDataSecurityManager.ValidationResult result = securityManager.validateEarlyData(packet, future);

        assertThat(result.isAccepted()).isFalse();
        assertThat(result.status()).isEqualTo(EarlyDataSecurityManager.ValidationStatus.REJECTED_TICKET_EXPIRED);
    }

    @Test
    @DisplayName("Timestamp with excessive clock skew is rejected")
    void testClockSkewExceededRejected() {
        Instant now = Instant.now();
        Instant skewedTime = now.minusSeconds(60); // 60s skew exceeds 10s window

        EarlyDataPacket packet = new EarlyDataPacket(
                activeTicket.ticketId(),
                "payload".getBytes(StandardCharsets.UTF_8),
                skewedTime,
                "GET",
                "/api/v1/data"
        );

        EarlyDataSecurityManager.ValidationResult result = securityManager.validateEarlyData(packet, now);

        assertThat(result.isAccepted()).isFalse();
        assertThat(result.status()).isEqualTo(EarlyDataSecurityManager.ValidationStatus.REJECTED_CLOCK_SKEW);
    }
}
