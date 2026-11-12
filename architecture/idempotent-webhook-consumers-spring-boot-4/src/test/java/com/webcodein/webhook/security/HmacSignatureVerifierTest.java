package com.webcodein.webhook.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HmacSignatureVerifierTest {

    private final String secret = "whsec_test_secret_key_123456";
    private final String payload = "{\"id\":\"evt_test_100\",\"amount\":5000}";

    @Test
    @DisplayName("Should return true when HMAC signature matches payload and secret")
    void testVerifyValidSignature() throws Exception {
        String validSig = generateSignature(payload, secret);
        assertTrue(HmacSignatureVerifier.verify(payload, validSig, secret));
    }

    @Test
    @DisplayName("Should return false when payload was altered")
    void testVerifyAlteredPayload() throws Exception {
        String validSig = generateSignature(payload, secret);
        String alteredPayload = "{\"id\":\"evt_test_100\",\"amount\":999999}";
        assertFalse(HmacSignatureVerifier.verify(alteredPayload, validSig, secret));
    }

    @Test
    @DisplayName("Should return false when secret is incorrect")
    void testVerifyIncorrectSecret() throws Exception {
        String validSig = generateSignature(payload, secret);
        assertFalse(HmacSignatureVerifier.verify(payload, validSig, "wrong_secret_key"));
    }

    @Test
    @DisplayName("Should return false when signature header is corrupted")
    void testVerifyCorruptedSignature() {
        assertFalse(HmacSignatureVerifier.verify(payload, "invalid_hex_signature", secret));
    }

    @Test
    @DisplayName("Should return false when signature header is null or empty")
    void testVerifyNullOrEmptySignature() {
        assertFalse(HmacSignatureVerifier.verify(payload, "", secret));
        assertFalse(HmacSignatureVerifier.verify(payload, null, secret));
    }

    private String generateSignature(String data, String key) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
