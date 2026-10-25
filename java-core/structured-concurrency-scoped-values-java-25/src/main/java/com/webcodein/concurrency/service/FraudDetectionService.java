package com.webcodein.concurrency.service;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.exception.FraudDetectedException;
import com.webcodein.concurrency.model.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FraudDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionService.class);

    public int evaluateRisk(String orderId, String customerId, BigDecimal amount) {
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Evaluating fraud risk for customer {} on order {} on thread: {}",
                context.correlationId(), customerId, orderId, Thread.currentThread());

        if ("SUSPICIOUS_USER".equalsIgnoreCase(customerId) || "BLOCKED_USER".equalsIgnoreCase(customerId)) {
            log.warn("[Correlation: {}] HIGH RISK DETECTED for user {}!", context.correlationId(), customerId);
            throw new FraudDetectedException("High fraud probability score (95/100) for flagged customer: " + customerId, 95);
        }

        try {
            // Simulate AI risk scoring latency
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Fraud evaluation interrupted for order " + orderId, e);
        }

        // Return low risk score
        return 12;
    }
}
