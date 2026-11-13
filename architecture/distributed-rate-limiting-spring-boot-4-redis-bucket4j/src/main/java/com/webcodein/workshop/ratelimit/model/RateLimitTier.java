package com.webcodein.workshop.ratelimit.model;

public enum RateLimitTier {
    FREE(10, 1),
    BASIC(60, 5),
    PREMIUM(300, 25),
    ENTERPRISE(1000, 100);

    private final long capacity;
    private final long refillPerSecond;

    RateLimitTier(long capacity, long refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getRefillPerSecond() {
        return refillPerSecond;
    }
}
