package com.webcodein.workshop.ratelimit;

import com.webcodein.workshop.ratelimit.model.RateLimitDecision;
import com.webcodein.workshop.ratelimit.model.RateLimitTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitTierTest {

    @Test
    @DisplayName("Tiers have valid increasing capacities")
    void verifyTierCapacities() {
        assertThat(RateLimitTier.FREE.getCapacity()).isEqualTo(10);
        assertThat(RateLimitTier.BASIC.getCapacity()).isEqualTo(60);
        assertThat(RateLimitTier.PREMIUM.getCapacity()).isEqualTo(300);
        assertThat(RateLimitTier.ENTERPRISE.getCapacity()).isEqualTo(1000);
    }

    @Test
    @DisplayName("RateLimitDecision factory methods build consistent records")
    void verifyDecisionFactory() {
        RateLimitDecision granted = RateLimitDecision.granted(9, RateLimitTier.FREE);
        assertThat(granted.allowed()).isTrue();
        assertThat(granted.remainingTokens()).isEqualTo(9);

        RateLimitDecision rejected = RateLimitDecision.rejected(1, RateLimitTier.FREE);
        assertThat(rejected.allowed()).isFalse();
        assertThat(rejected.retryAfterSeconds()).isEqualTo(1);
    }
}
