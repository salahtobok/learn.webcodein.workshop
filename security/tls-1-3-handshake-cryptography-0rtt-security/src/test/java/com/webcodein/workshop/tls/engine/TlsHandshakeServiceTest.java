package com.webcodein.workshop.tls.engine;

import com.webcodein.workshop.tls.model.CipherSuite;
import com.webcodein.workshop.tls.model.EarlyDataPacket;
import com.webcodein.workshop.tls.model.HandshakeResult;
import com.webcodein.workshop.tls.model.NamedGroup;
import com.webcodein.workshop.tls.model.SessionTicket;
import com.webcodein.workshop.tls.model.TlsVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TlsHandshakeServiceTest {

    private EarlyDataSecurityManager securityManager;
    private TlsHandshakeService handshakeService;

    @BeforeEach
    void setUp() {
        securityManager = new EarlyDataSecurityManager();
        handshakeService = new TlsHandshakeService(securityManager);
    }

    @Test
    @DisplayName("Full TLS 1.3 handshake succeeds in 1 RTT with valid key derivation")
    void testFullHandshakeTls13() {
        HandshakeResult result = handshakeService.performFullHandshake(
                NamedGroup.SECP256R1,
                CipherSuite.TLS_AES_128_GCM_SHA256
        );

        assertThat(result.version()).isEqualTo(TlsVersion.TLS_1_3);
        assertThat(result.cipherSuite()).isEqualTo(CipherSuite.TLS_AES_128_GCM_SHA256);
        assertThat(result.roundTrips()).isEqualTo(1);
        assertThat(result.earlyDataAccepted()).isFalse();
        assertThat(result.clientTrafficSecret()).isNotEmpty();
        assertThat(result.serverTrafficSecret()).isNotEmpty();
        assertThat(result.sessionTicketId()).isNotNull();
    }

    @Test
    @DisplayName("0-RTT handshake succeeds with 0 round-trips when session ticket is valid")
    void testZeroRttHandshakeSuccess() {
        byte[] masterSecret = "shared-master-secret-32-bytes-ok".getBytes(StandardCharsets.UTF_8);
        SessionTicket ticket = securityManager.issueTicket(
                CipherSuite.TLS_AES_128_GCM_SHA256,
                masterSecret,
                Duration.ofHours(2)
        );

        Instant now = Instant.now();
        EarlyDataPacket earlyPacket = new EarlyDataPacket(
                ticket.ticketId(),
                "encrypted-client-early-data".getBytes(StandardCharsets.UTF_8),
                now,
                "GET",
                "/api/v1/products"
        );

        HandshakeResult result = handshakeService.performZeroRttHandshake(ticket, earlyPacket, now);

        assertThat(result.roundTrips()).isEqualTo(0);
        assertThat(result.earlyDataAccepted()).isTrue();
        assertThat(result.clientTrafficSecret()).isNotEmpty();
    }

    @Test
    @DisplayName("0-RTT handshake falls back to 1-RTT when replay is detected")
    void testZeroRttHandshakeReplayFallback() {
        byte[] masterSecret = "shared-master-secret-32-bytes-ok".getBytes(StandardCharsets.UTF_8);
        SessionTicket ticket = securityManager.issueTicket(
                CipherSuite.TLS_AES_128_GCM_SHA256,
                masterSecret,
                Duration.ofHours(2)
        );

        Instant now = Instant.now();
        EarlyDataPacket earlyPacket = new EarlyDataPacket(
                ticket.ticketId(),
                "payload".getBytes(StandardCharsets.UTF_8),
                now,
                "GET",
                "/api/v1/products"
        );

        // First use: accepted (0-RTT)
        HandshakeResult result1 = handshakeService.performZeroRttHandshake(ticket, earlyPacket, now);
        assertThat(result1.roundTrips()).isEqualTo(0);
        assertThat(result1.earlyDataAccepted()).isTrue();

        // Second use: replay detected, fallback to 1-RTT
        HandshakeResult result2 = handshakeService.performZeroRttHandshake(ticket, earlyPacket, now.plusSeconds(1));
        assertThat(result2.roundTrips()).isEqualTo(1);
        assertThat(result2.earlyDataAccepted()).isFalse();
    }

    @Test
    @DisplayName("Attempting full handshake with TLS 1.2 legacy cipher suite throws IllegalArgumentException")
    void testLegacyCipherSuiteRejected() {
        assertThatThrownBy(() -> handshakeService.performFullHandshake(
                NamedGroup.SECP256R1,
                CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not compliant with TLS 1.3");
    }
}
