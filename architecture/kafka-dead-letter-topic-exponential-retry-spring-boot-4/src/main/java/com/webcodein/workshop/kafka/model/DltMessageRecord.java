package com.webcodein.workshop.kafka.model;

import java.time.Instant;
import java.util.Objects;

public record DltMessageRecord(
        String id,
        String originalTopic,
        int partition,
        long offset,
        String payload,
        String exceptionMessage,
        Instant timestamp,
        boolean replayed
) {
    public DltMessageRecord {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(originalTopic, "originalTopic must not be null");
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(exceptionMessage, "exceptionMessage must not be null");
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public DltMessageRecord withReplayed() {
        return new DltMessageRecord(id, originalTopic, partition, offset, payload, exceptionMessage, timestamp, true);
    }
}
