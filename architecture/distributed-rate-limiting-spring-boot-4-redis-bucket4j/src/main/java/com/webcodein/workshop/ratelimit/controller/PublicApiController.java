package com.webcodein.workshop.ratelimit.controller;

import com.webcodein.workshop.ratelimit.model.RateLimitDecision;
import com.webcodein.workshop.ratelimit.model.RateLimitTier;
import com.webcodein.workshop.ratelimit.service.ClientResolverService;
import com.webcodein.workshop.ratelimit.service.TokenBucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PublicApiController {

    private final TokenBucketService tokenBucketService;
    private final ClientResolverService clientResolverService;

    public PublicApiController(TokenBucketService tokenBucketService, ClientResolverService clientResolverService) {
        this.tokenBucketService = tokenBucketService;
        this.clientResolverService = clientResolverService;
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData(
            @RequestHeader(value = "X-API-KEY", required = false) String apiKey,
            @RequestHeader(value = "X-Forwarded-For", required = false, defaultValue = "127.0.0.1") String clientIp
    ) {
        RateLimitTier tier = clientResolverService.resolveTier(apiKey);
        String clientKey = clientResolverService.resolveClientKey(apiKey, clientIp);

        RateLimitDecision decision = tokenBucketService.tryConsume(clientKey, tier);

        if (!decision.allowed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", String.valueOf(decision.retryAfterSeconds()))
                    .header("X-RateLimit-Limit", String.valueOf(tier.getCapacity()))
                    .header("X-RateLimit-Remaining", "0")
                    .body(Map.of(
                            "error", "Too Many Requests",
                            "message", "Rate limit exceeded for tier " + tier.name(),
                            "retryAfterSeconds", decision.retryAfterSeconds()
                    ));
        }

        return ResponseEntity.ok()
                .header("X-RateLimit-Limit", String.valueOf(tier.getCapacity()))
                .header("X-RateLimit-Remaining", String.valueOf(decision.remainingTokens()))
                .body(Map.of(
                        "status", "SUCCESS",
                        "tier", tier.name(),
                        "remainingTokens", decision.remainingTokens(),
                        "payload", "High-throughput enterprise payload data"
                ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "rateLimiter", "Token Bucket Architecture with Redis compatibility"
        ));
    }
}
