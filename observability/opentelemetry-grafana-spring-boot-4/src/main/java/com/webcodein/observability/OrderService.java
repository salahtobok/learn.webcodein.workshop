package com.webcodein.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final ObservationRegistry observationRegistry;
    private final Random random = new Random();

    public OrderService(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public String processOrder(String orderId) {
        return Observation.createNotStarted("process.order", observationRegistry)
                .lowCardinalityKeyValue("order.type", "standard")
                .observe(() -> {
                    log.info("Processing order: {}", orderId);
                    simulateWork();
                    
                    if (random.nextInt(10) > 8) {
                        log.error("Failed to process order: {}", orderId);
                        throw new RuntimeException("Simulated order processing failure");
                    }
                    
                    log.info("Order processed successfully: {}", orderId);
                    return "Processed " + orderId;
                });
    }

    private void simulateWork() {
        try {
            Thread.sleep(100 + random.nextInt(400));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
