package com.webcodein.concurrency.service;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.exception.PaymentDeclinedException;
import com.webcodein.concurrency.model.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public String processPayment(String orderId, BigDecimal amount) {
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Processing payment of ${} for order {} (tenant: {}) on thread: {}",
                context.correlationId(), amount, orderId, context.tenantId(), Thread.currentThread());

        if (amount.compareTo(new BigDecimal("50000.00")) > 0) {
            throw new PaymentDeclinedException("Transaction exceeds single payment limit: $" + amount);
        }

        try {
            // Simulate network call to payment gateway
            Thread.sleep(60);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Payment authorization interrupted for order " + orderId, e);
        }

        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
