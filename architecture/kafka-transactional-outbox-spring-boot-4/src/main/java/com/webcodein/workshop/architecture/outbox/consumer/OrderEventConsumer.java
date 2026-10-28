package com.webcodein.workshop.architecture.outbox.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final List<String> receivedPayloads = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "order-events", groupId = "order-audit-group")
    public void consume(String payload) {
        log.info("Received Kafka order event: {}", payload);
        receivedPayloads.add(payload);
    }

    public List<String> getReceivedPayloads() {
        return receivedPayloads;
    }

    public void clear() {
        receivedPayloads.clear();
    }
}
