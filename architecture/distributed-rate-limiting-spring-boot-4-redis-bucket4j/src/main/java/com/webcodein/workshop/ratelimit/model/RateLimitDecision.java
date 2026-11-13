package com.webcodein.workshop.ratelimit.model;

import java.util.Objects;

public record RateLimitDecision(
        boolean allowed,
        long remainingTokens,
        long retryAfterSeconds,
        RateLimitTier tier
) {
    public RateLimitDecision {
        Objects.requireNonNull(tier, "Tier must not be null");
    }

    public static RateLimitDecision granted(long remainingTokens, RateLimitTier tier) {
        return new RateLimitDecision(true, remainingTokens, 0L, tier);
    }

    public static RateLimitDecision rejected(long retryAfterSeconds, RateLimitTier tier) {
        return new RateLimitDecision(false, 0L, retryAfterSeconds, tier);
    }
}
