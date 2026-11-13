package com.webcodein.workshop.kafka.service;

import com.webcodein.workshop.kafka.model.DltMessageRecord;
import com.webcodein.workshop.kafka.model.OrderEvent;
import com.webcodein.workshop.kafka.model.RetryStats;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class KafkaRetryConsumerService {

    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final String MAIN_TOPIC = "orders.v1";
    public static final String DLT_TOPIC = "orders-dlt";

    private final Map<String, DltMessageRecord> dltStore = new ConcurrentHashMap<>();
    private final AtomicLong totalProcessedCounter = new AtomicLong(0);
    private final AtomicLong successfulFirstCounter = new AtomicLong(0);
    private final AtomicLong retriedSuccessCounter = new AtomicLong(0);
    private final AtomicLong deadLetterCounter = new AtomicLong(0);

    public String processOrderEvent(OrderEvent event) {
        totalProcessedCounter.incrementAndGet();

        // 1. Poison pill simulation: orderId containing "POISON" causes non-retryable fatal error
        if (event.orderId().contains("POISON")) {
            routeToDlt(event, "Fatal non-retryable error: Poison pill detected");
            return "ROUTED_TO_DLT_FATAL";
        }

        // 2. Transient error simulation: orderId containing "FAIL" fails initially
        if (event.orderId().contains("FAIL")) {
            if (event.retryCount() < MAX_RETRY_ATTEMPTS) {
                // Route to next exponential retry topic
                int nextDelay = calculateDelayMs(event.retryCount() + 1);
                String retryTopic = "orders-retry-" + nextDelay;
                return "FORWARDED_TO_RETRY_TOPIC:" + retryTopic;
            } else {
                // Retries exhausted, route to DLT
                routeToDlt(event, "Retries exhausted after " + MAX_RETRY_ATTEMPTS + " attempts");
                return "ROUTED_TO_DLT_EXHAUSTED";
            }
        }

        // 3. Successful processing
        if (event.retryCount() == 0) {
            successfulFirstCounter.incrementAndGet();
        } else {
            retriedSuccessCounter.incrementAndGet();
        }
        return "PROCESSED_SUCCESSFULLY";
    }

    public int calculateDelayMs(int attempt) {
        // Exponential backoff: 1000ms, 2000ms, 4000ms
        return (int) (1000 * Math.pow(2, attempt - 1));
    }

    private void routeToDlt(OrderEvent event, String reason) {
        deadLetterCounter.incrementAndGet();
        String dltId = "DLT-" + UUID.randomUUID().toString().substring(0, 8);
        DltMessageRecord record = new DltMessageRecord(
                dltId,
                MAIN_TOPIC,
                0,
                System.currentTimeMillis(),
                event.orderId() + "|" + event.customerId() + "|" + event.amount(),
                reason,
                Instant.now(),
                false
        );
        dltStore.put(dltId, record);
    }

    public List<DltMessageRecord> getAllDltMessages() {
        return Collections.unmodifiableList(new ArrayList<>(dltStore.values()));
    }

    public Optional<DltMessageRecord> findDltMessage(String id) {
        return Optional.ofNullable(dltStore.get(id));
    }

    public boolean replayMessage(String id) {
        DltMessageRecord record = dltStore.get(id);
        if (record != null && !record.replayed()) {
            dltStore.put(id, record.withReplayed());
            return true;
        }
        return false;
    }

    public RetryStats getStats() {
        return new RetryStats(
                totalProcessedCounter.get(),
                successfulFirstCounter.get(),
                retriedSuccessCounter.get(),
                deadLetterCounter.get()
        );
    }
}
