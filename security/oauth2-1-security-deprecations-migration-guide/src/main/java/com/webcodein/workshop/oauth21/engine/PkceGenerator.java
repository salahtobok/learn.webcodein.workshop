package com.webcodein.workshop.oauth21.engine;

import com.webcodein.workshop.oauth21.model.PkceChallenge;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PkceGenerator {

    private static final int DEFAULT_VERIFIER_BYTE_LENGTH = 32; // Generates 43 Base64URL chars
    private final SecureRandom secureRandom;

    public PkceGenerator() {
        this.secureRandom = new SecureRandom();
    }

    public String generateCodeVerifier() {
        return generateCodeVerifier(DEFAULT_VERIFIER_BYTE_LENGTH);
    }

    public String generateCodeVerifier(int byteLength) {
        if (byteLength < 32 || byteLength > 96) {
            throw new IllegalArgumentException("Verifier byte length must be between 32 and 96 to produce 43-128 characters");
        }
        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String computeCodeChallenge(String codeVerifier) {
        if (codeVerifier == null || codeVerifier.length() < 43 || codeVerifier.length() > 128) {
            throw new IllegalArgumentException("code_verifier must be between 43 and 128 characters per RFC 7636");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    public PkceChallenge createChallenge() {
        String verifier = generateCodeVerifier();
        String challenge = computeCodeChallenge(verifier);
        return new PkceChallenge(verifier, challenge, "S256");
    }
}
