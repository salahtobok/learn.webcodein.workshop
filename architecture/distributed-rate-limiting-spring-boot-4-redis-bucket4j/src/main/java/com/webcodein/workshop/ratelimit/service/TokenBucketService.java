package com.webcodein.workshop.ratelimit.service;

import com.webcodein.workshop.ratelimit.model.RateLimitDecision;
import com.webcodein.workshop.ratelimit.model.RateLimitTier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TokenBucketService {

    private final Map<String, BucketState> buckets = new ConcurrentHashMap<>();

    public RateLimitDecision tryConsume(String clientKey, RateLimitTier tier) {
        BucketState state = buckets.computeIfAbsent(clientKey, k -> new BucketState(tier.getCapacity(), System.currentTimeMillis()));

        synchronized (state) {
            long now = System.currentTimeMillis();
            long elapsedSeconds = Math.max(0, (now - state.lastRefillTime) / 1000);

            if (elapsedSeconds > 0) {
                long tokensToAdd = elapsedSeconds * tier.getRefillPerSecond();
                state.tokens = Math.min(tier.getCapacity(), state.tokens + tokensToAdd);
                state.lastRefillTime = now;
            }

            if (state.tokens >= 1) {
                state.tokens--;
                return RateLimitDecision.granted(state.tokens, tier);
            } else {
                long retryAfter = Math.max(1, (1 / Math.max(1, tier.getRefillPerSecond())));
                return RateLimitDecision.rejected(retryAfter, tier);
            }
        }
    }

    public void reset() {
        buckets.clear();
    }

    private static class BucketState {
        long tokens;
        long lastRefillTime;

        BucketState(long tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}
