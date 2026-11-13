package com.webcodein.workshop.csp.engine;

import java.security.SecureRandom;
import java.util.Base64;

public class CspNonceGenerator {

    private static final int DEFAULT_BYTE_LENGTH = 16; // 128 bits of entropy
    private final SecureRandom secureRandom;

    public CspNonceGenerator() {
        this.secureRandom = new SecureRandom();
    }

    public String generateNonce() {
        return generateNonce(DEFAULT_BYTE_LENGTH);
    }

    public String generateNonce(int byteLength) {
        if (byteLength < 16) {
            throw new IllegalArgumentException("Nonce length must be at least 16 bytes (128 bits) per CSP Level 3 specification");
        }
        byte[] nonceBytes = new byte[byteLength];
        secureRandom.nextBytes(nonceBytes);
        return Base64.getEncoder().encodeToString(nonceBytes);
    }
}
