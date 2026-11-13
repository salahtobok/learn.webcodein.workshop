package com.webcodein.workshop.tls.engine;

import com.webcodein.workshop.tls.model.CipherSuite;
import com.webcodein.workshop.tls.model.EarlyDataPacket;
import com.webcodein.workshop.tls.model.SessionTicket;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages 0-RTT Early Data session resumption and RFC 8446 Section 8 anti-replay mitigations.
 */
public class EarlyDataSecurityManager {

    public enum ValidationStatus {
        ACCEPTED,
        REJECTED_REPLAY_DETECTED,
        REJECTED_TICKET_EXPIRED,
        REJECTED_CLOCK_SKEW,
        REJECTED_NON_IDEMPOTENT_METHOD,
        REJECTED_TICKET_NOT_FOUND
    }

    public record ValidationResult(ValidationStatus status, String message, SessionTicket ticket) {
        public boolean isAccepted() {
            return status == ValidationStatus.ACCEPTED;
        }
    }

    private static final Duration MAX_ALLOWED_CLOCK_SKEW = Duration.ofSeconds(10);
    private static final int DEFAULT_MAX_EARLY_DATA_SIZE = 16384; // 16 KB

    // Active session tickets issued by the server
    private final Map<String, SessionTicket> issuedTickets = new ConcurrentHashMap<>();

    // Anti-replay single-use ticket cache: records tickets already redeemed
    private final Set<String> redeemedTickets = ConcurrentHashMap.newKeySet();

    public SessionTicket issueTicket(CipherSuite cipherSuite, byte[] resumptionMasterSecret, Duration lifetime) {
        String ticketId = "tkt_" + UUID.randomUUID();
        SessionTicket ticket = new SessionTicket(
                ticketId,
                resumptionMasterSecret,
                Instant.now(),
                lifetime,
                DEFAULT_MAX_EARLY_DATA_SIZE,
                cipherSuite
        );
        issuedTickets.put(ticketId, ticket);
        return ticket;
    }

    public ValidationResult validateEarlyData(EarlyDataPacket packet, Instant serverCurrentTime) {
        Objects.requireNonNull(packet, "packet must not be null");
        Objects.requireNonNull(serverCurrentTime, "serverCurrentTime must not be null");

        // 1. Anti-Replay Defense: Check single-use ticket cache
        if (redeemedTickets.contains(packet.ticketId())) {
            return new ValidationResult(
                    ValidationStatus.REJECTED_REPLAY_DETECTED,
                    "Replay attack detected: session ticket " + packet.ticketId() + " has already been redeemed",
                    null
            );
        }

        // 2. Existence check
        SessionTicket ticket = issuedTickets.get(packet.ticketId());
        if (ticket == null) {
            return new ValidationResult(
                    ValidationStatus.REJECTED_TICKET_NOT_FOUND,
                    "Session ticket " + packet.ticketId() + " does not exist or was evicted",
                    null
            );
        }

        // 3. Expiration check
        if (ticket.isExpired(serverCurrentTime)) {
            return new ValidationResult(
                    ValidationStatus.REJECTED_TICKET_EXPIRED,
                    "Session ticket " + packet.ticketId() + " has expired",
                    ticket
            );
        }

        // 4. Clock skew / freshness window check
        Duration skew = Duration.between(packet.clientTimestamp(), serverCurrentTime).abs();
        if (skew.compareTo(MAX_ALLOWED_CLOCK_SKEW) > 0) {
            return new ValidationResult(
                    ValidationStatus.REJECTED_CLOCK_SKEW,
                    "Client timestamp deviates by " + skew.toMillis() + "ms (max allowed: " + MAX_ALLOWED_CLOCK_SKEW.toMillis() + "ms)",
                    ticket
            );
        }

        // 5. Method safety check: RFC 8446 Section 8 rejects early data replay for non-idempotent HTTP methods
        if (!packet.isSafeIdempotentMethod()) {
            return new ValidationResult(
                    ValidationStatus.REJECTED_NON_IDEMPOTENT_METHOD,
                    "Unsafe HTTP method '" + packet.httpMethod() + "' rejected for 0-RTT early data to prevent replay side-effects",
                    ticket
            );
        }

        // Single-use guarantee: mark ticket as redeemed
        redeemedTickets.add(packet.ticketId());

        return new ValidationResult(
                ValidationStatus.ACCEPTED,
                "0-RTT early data session resumption validated successfully",
                ticket
        );
    }

    public boolean isTicketRedeemed(String ticketId) {
        return redeemedTickets.contains(ticketId);
    }

    public int getActiveTicketCount() {
        return issuedTickets.size();
    }
}
