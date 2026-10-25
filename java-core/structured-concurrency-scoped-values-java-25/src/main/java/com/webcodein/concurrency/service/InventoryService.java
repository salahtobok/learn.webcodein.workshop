package com.webcodein.concurrency.service;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.exception.InventoryUnavailableException;
import com.webcodein.concurrency.model.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public String reserveItems(String orderId, int itemCount) {
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Reserving {} items for order {} (user: {}) on thread: {}",
                context.correlationId(), itemCount, orderId, context.userId(), Thread.currentThread());

        if (itemCount > 100) {
            throw new InventoryUnavailableException("Insufficient stock for item count: " + itemCount);
        }

        try {
            // Simulate I/O latency
            Thread.sleep(40);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Inventory reservation interrupted for order " + orderId, e);
        }

        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
