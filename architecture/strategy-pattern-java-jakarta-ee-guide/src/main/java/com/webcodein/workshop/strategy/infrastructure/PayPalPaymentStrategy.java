package com.webcodein.workshop.strategy.infrastructure;

import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import com.webcodein.workshop.strategy.domain.PaymentStrategy;
import com.webcodein.workshop.strategy.domain.PaymentType;
import jakarta.inject.Named;

import java.util.UUID;
import java.util.logging.Logger;

@Named("payPalStrategy")
public class PayPalPaymentStrategy implements PaymentStrategy {
    private static final Logger log = Logger.getLogger(PayPalPaymentStrategy.class.getName());

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.PAYPAL;
    }

    @Override
    public PaymentResult process(PaymentRequest request) {
        log.info("Redirecting to PayPal for amount $" + request.amount() + " for customer " + request.customerId());
        return new PaymentResult(true, "PP-" + UUID.randomUUID().toString(), "PayPal transaction completed");
    }
}
