package com.webcodein.webhook.controller;

import com.webcodein.webhook.model.WebhookEvent;
import com.webcodein.webhook.repository.WebhookEventRepository;
import com.webcodein.webhook.security.HmacSignatureVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookEventRepository repository;
    private final String webhookSecret;

    public WebhookController(WebhookEventRepository repository, 
                             @Value("${webhook.secret}") String webhookSecret) {
        this.repository = repository;
        this.webhookSecret = webhookSecret;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestHeader("Stripe-Signature") String signature,
            @RequestBody String rawPayload) {
            
        if (!HmacSignatureVerifier.verify(rawPayload, signature, webhookSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String eventId = "unknown";
        try {
            Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(rawPayload);
            if (matcher.find()) {
                eventId = matcher.group(1);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        try {
            WebhookEvent event = new WebhookEvent(eventId, rawPayload);
            repository.save(event);
        } catch (DataIntegrityViolationException e) {
            // Event already exists. We must return 200 OK to stop retries.
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.accepted().build();
    }
}
