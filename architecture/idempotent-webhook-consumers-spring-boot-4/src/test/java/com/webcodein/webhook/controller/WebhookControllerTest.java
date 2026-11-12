package com.webcodein.webhook.controller;

import com.webcodein.webhook.repository.WebhookEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@TestPropertySource(properties = "webhook.secret=test_controller_secret_xyz")
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookEventRepository repository;

    private final String secret = "test_controller_secret_xyz";

    @Test
    @DisplayName("POST /api/webhooks/stripe returns 202 Accepted on valid signature and new event")
    void testValidWebhookAccepted() throws Exception {
        String payload = "{\"id\":\"evt_new_001\",\"type\":\"payment.created\"}";
        String signature = generateSignature(payload, secret);

        when(repository.existsById("evt_new_001")).thenReturn(false);

        mockMvc.perform(post("/api/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .content(payload))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("POST /api/webhooks/stripe returns 200 OK on existing duplicate event")
    void testDuplicateWebhookReturnsOk() throws Exception {
        String payload = "{\"id\":\"evt_existing_002\",\"type\":\"payment.created\"}";
        String signature = generateSignature(payload, secret);

        when(repository.existsById("evt_existing_002")).thenReturn(true);

        mockMvc.perform(post("/api/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/webhooks/stripe returns 200 OK on concurrent DataIntegrityViolationException")
    void testConcurrentDuplicateReturnsOk() throws Exception {
        String payload = "{\"id\":\"evt_concurrent_003\",\"type\":\"payment.created\"}";
        String signature = generateSignature(payload, secret);

        when(repository.existsById("evt_concurrent_003")).thenReturn(false);
        when(repository.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate key"));

        mockMvc.perform(post("/api/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .content(payload))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/webhooks/stripe returns 401 Unauthorized on invalid signature")
    void testInvalidSignatureUnauthorized() throws Exception {
        String payload = "{\"id\":\"evt_004\",\"type\":\"payment.created\"}";

        mockMvc.perform(post("/api/webhooks/stripe")
                .header("Stripe-Signature", "invalid_signature_hex")
                .content(payload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/webhooks/stripe returns 400 Bad Request when JSON lacks id")
    void testMissingIdBadRequest() throws Exception {
        String payload = "{\"type\":\"payment.created\"}";
        String signature = generateSignature(payload, secret);

        mockMvc.perform(post("/api/webhooks/stripe")
                .header("Stripe-Signature", signature)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    private String generateSignature(String data, String key) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
