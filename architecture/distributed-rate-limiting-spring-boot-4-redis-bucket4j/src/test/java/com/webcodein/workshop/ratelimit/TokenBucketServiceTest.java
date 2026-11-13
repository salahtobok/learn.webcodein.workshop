package com.webcodein.workshop.ratelimit;

import com.webcodein.workshop.ratelimit.model.RateLimitDecision;
import com.webcodein.workshop.ratelimit.model.RateLimitTier;
import com.webcodein.workshop.ratelimit.service.TokenBucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBucketServiceTest {

    private TokenBucketService service;

    @BeforeEach
    void setUp() {
        service = new TokenBucketService();
    }

    @Test
    @DisplayName("Should grant tokens up to capacity")
    void shouldConsumeTokens() {
        RateLimitDecision d1 = service.tryConsume("user-1", RateLimitTier.FREE);
        assertThat(d1.allowed()).isTrue();
        assertThat(d1.remainingTokens()).isEqualTo(9);

        RateLimitDecision d2 = service.tryConsume("user-1", RateLimitTier.FREE);
        assertThat(d2.allowed()).isTrue();
        assertThat(d2.remainingTokens()).isEqualTo(8);
    }

    @Test
    @DisplayName("Should reject request when bucket is empty")
    void shouldRejectWhenExhausted() {
        for (int i = 0; i < 10; i++) {
            service.tryConsume("user-exhaust", RateLimitTier.FREE);
        }

        RateLimitDecision rejected = service.tryConsume("user-exhaust", RateLimitTier.FREE);
        assertThat(rejected.allowed()).isFalse();
        assertThat(rejected.remainingTokens()).isEqualTo(0);
        assertThat(rejected.retryAfterSeconds()).isGreaterThan(0);
    }
}
