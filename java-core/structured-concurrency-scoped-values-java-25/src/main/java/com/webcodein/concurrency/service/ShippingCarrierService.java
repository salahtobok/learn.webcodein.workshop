package com.webcodein.concurrency.service;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.model.RequestContext;
import com.webcodein.concurrency.model.ShippingQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ShippingCarrierService {

    private static final Logger log = LoggerFactory.getLogger(ShippingCarrierService.class);

    public ShippingQuote fetchPrimaryCarrierQuote(String origin, String destination) {
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Calling ExpressCarrier API on thread: {}",
                context.correlationId(), Thread.currentThread());

        try {
            // Fast carrier: responds in 40ms
            Thread.sleep(40);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Primary carrier quote interrupted", e);
        }

        return new ShippingQuote(
                "FastExpress Logistics",
                new BigDecimal("24.50"),
                2,
                Instant.now(),
                context.correlationId()
        );
    }

    public ShippingQuote fetchSecondaryCarrierQuote(String origin, String destination) {
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Calling BudgetFreight API on thread: {}",
                context.correlationId(), Thread.currentThread());

        try {
            // Slower carrier: responds in 200ms
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Secondary carrier quote interrupted", e);
        }

        return new ShippingQuote(
                "BudgetFreight Cargo",
                new BigDecimal("18.99"),
                5,
                Instant.now(),
                context.correlationId()
        );
    }
}
