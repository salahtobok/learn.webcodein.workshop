package com.webcodein.workshop.kafka.model;

public record RetryStats(
        long totalProcessed,
        long successfulFirstAttempt,
        long retriedSuccess,
        long deadLetterCount
) {
    public RetryStats {
        if (totalProcessed < 0 || successfulFirstAttempt < 0 || retriedSuccess < 0 || deadLetterCount < 0) {
            throw new IllegalArgumentException("Stats counters cannot be negative");
        }
    }
}
