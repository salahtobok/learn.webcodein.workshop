package com.webcodein.workshop.tls.engine;

import com.webcodein.workshop.tls.crypto.HkdfEngine;
import com.webcodein.workshop.tls.crypto.KeyExchangeEngine;
import com.webcodein.workshop.tls.crypto.KeyExchangeEngine.KeyExchangePair;
import com.webcodein.workshop.tls.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class TlsHandshakeService {

    private final EarlyDataSecurityManager securityManager;

    public TlsHandshakeService(EarlyDataSecurityManager securityManager) {
        this.securityManager = Objects.requireNonNull(securityManager, "securityManager must not be null");
    }

    public HandshakeResult performFullHandshake(NamedGroup group, CipherSuite cipherSuite) {
        if (!cipherSuite.isTls13Allowed()) {
            throw new IllegalArgumentException("CipherSuite " + cipherSuite + " is not compliant with TLS 1.3");
        }

        // 1. Ephemeral Key Exchange (ECDHE / X25519)
        KeyExchangePair clientKeys = KeyExchangeEngine.generateKeyPair(group);
        KeyExchangePair serverKeys = KeyExchangeEngine.generateKeyPair(group);

        byte[] clientSharedSecret = KeyExchangeEngine.computeSharedSecret(
                clientKeys.getPrivateKey(), serverKeys.getPublicKey(), group);
        byte[] serverSharedSecret = KeyExchangeEngine.computeSharedSecret(
                serverKeys.getPrivateKey(), clientKeys.getPublicKey(), group);

        if (!MessageDigest.isEqual(clientSharedSecret, serverSharedSecret)) {
            throw new IllegalStateException("Client and Server shared secret mismatch");
        }

        // 2. Simulated Transcript Hash (ClientHello + ServerHello)
        byte[] transcriptHash = computeDigest(
                ("transcript-" + clientKeys.getPublicKey().toString() + serverKeys.getPublicKey().toString())
                        .getBytes(StandardCharsets.UTF_8),
                cipherSuite.getHmacAlgorithm().equals("HmacSHA384") ? "SHA-384" : "SHA-256"
        );

        // 3. RFC 8446 Section 7.1 Key Schedule Derivation
        String hmac = cipherSuite.getHmacAlgorithm();
        int hashLen = cipherSuite.getKeyLengthBytes();
        byte[] zeros = new byte[hashLen];

        // Early Secret = HKDF-Extract(0, 0)
        byte[] earlySecret = HkdfEngine.extract(zeros, zeros, hmac);

        // Derived Secret = Derive-Secret(Early Secret, "derived", "")
        byte[] derivedFromEarly = HkdfEngine.deriveSecret(earlySecret, "derived", new byte[0], hmac);

        // Handshake Secret = HKDF-Extract(Derived Secret, Shared Secret)
        byte[] handshakeSecret = HkdfEngine.extract(derivedFromEarly, clientSharedSecret, hmac);

        // Client and Server Handshake Traffic Secrets
        byte[] clientHsTraffic = HkdfEngine.deriveSecret(handshakeSecret, "c hs traffic", transcriptHash, hmac);
        byte[] serverHsTraffic = HkdfEngine.deriveSecret(handshakeSecret, "s hs traffic", transcriptHash, hmac);

        // Master Secret = HKDF-Extract(Derive-Secret(Handshake Secret, "derived", ""), 0)
        byte[] derivedFromHs = HkdfEngine.deriveSecret(handshakeSecret, "derived", new byte[0], hmac);
        byte[] masterSecret = HkdfEngine.extract(derivedFromHs, zeros, hmac);

        // Application Traffic Secrets
        byte[] clientAppTraffic = HkdfEngine.deriveSecret(masterSecret, "c ap traffic", transcriptHash, hmac);
        byte[] serverAppTraffic = HkdfEngine.deriveSecret(masterSecret, "s ap traffic", transcriptHash, hmac);

        // Resumption Master Secret (for session tickets & 0-RTT)
        byte[] resumptionMasterSecret = HkdfEngine.deriveSecret(masterSecret, "res master", transcriptHash, hmac);

        // 4. Server issues Session Ticket for 0-RTT resumption
        SessionTicket ticket = securityManager.issueTicket(cipherSuite, resumptionMasterSecret, Duration.ofHours(24));

        return new HandshakeResult(
                TlsVersion.TLS_1_3,
                cipherSuite,
                1, // TLS 1.3 1-RTT Handshake
                clientAppTraffic,
                serverAppTraffic,
                false,
                ticket.ticketId()
        );
    }

    public HandshakeResult performZeroRttHandshake(SessionTicket ticket, EarlyDataPacket earlyDataPacket, Instant serverTime) {
        EarlyDataSecurityManager.ValidationResult validation = securityManager.validateEarlyData(earlyDataPacket, serverTime);

        if (validation.isAccepted()) {
            // 0-RTT Resumption Accepted: early data payload processed without round-trip delay!
            String hmac = ticket.cipherSuite().getHmacAlgorithm();
            byte[] clientEarlyTrafficSecret = HkdfEngine.deriveSecret(
                    ticket.resumptionMasterSecret(),
                    "c e traffic",
                    computeDigest(earlyDataPacket.ticketId().getBytes(StandardCharsets.UTF_8), "SHA-256"),
                    hmac
            );

            return new HandshakeResult(
                    TlsVersion.TLS_1_3,
                    ticket.cipherSuite(),
                    0, // 0-RTT Zero Round Trip
                    clientEarlyTrafficSecret,
                    ticket.resumptionMasterSecret(),
                    true,
                    ticket.ticketId()
            );
        }

        // Replay, expiration, clock skew, or unsafe method detected: Fall back to 1-RTT full handshake
        HandshakeResult fallbackResult = performFullHandshake(NamedGroup.SECP256R1, ticket.cipherSuite());
        return new HandshakeResult(
                fallbackResult.version(),
                fallbackResult.cipherSuite(),
                1, // Fallback to 1-RTT
                fallbackResult.clientTrafficSecret(),
                fallbackResult.serverTrafficSecret(),
                false,
                fallbackResult.sessionTicketId()
        );
    }

    private static byte[] computeDigest(byte[] input, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Digest algorithm not available: " + algorithm, e);
        }
    }
}
