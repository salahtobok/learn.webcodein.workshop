package com.webcodein.webhook;

import com.webcodein.webhook.model.EventStatus;
import com.webcodein.webhook.repository.WebhookEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class WebhookIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private WebhookEventRepository repository;
    
    @Value("${webhook.secret}")
    private String secret;

    @Test
    void testIdempotentWebhookProcessing() throws Exception {
        String payload = "{\"id\":\"evt_12345\", \"type\":\"payment_intent.succeeded\"}";
        String signature = generateSignature(payload, secret);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/webhooks/stripe"))
                .header("Stripe-Signature", signature)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        // First Request
        HttpResponse<Void> response1 = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertThat(response1.statusCode()).isEqualTo(202);
        
        // Allow time for async processing
        Thread.sleep(2000);
        
        assertThat(repository.findById("evt_12345")).isPresent();
        assertThat(repository.findById("evt_12345").get().getStatus()).isEqualTo(EventStatus.PROCESSED);

        // Second Request (Duplicate)
        HttpResponse<Void> response2 = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertThat(response2.statusCode()).isEqualTo(200);
        
        // Still only one event in DB
        assertThat(repository.count()).isEqualTo(1);
    }
    
    private String generateSignature(String payload, String secret) throws Exception {
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256HMAC.init(secretKey);
        byte[] hash = sha256HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}
