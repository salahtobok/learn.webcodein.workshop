package com.webcodein.workshop.strategy.infrastructure;

import com.webcodein.workshop.strategy.domain.PaymentRequest;
import com.webcodein.workshop.strategy.domain.PaymentResult;
import com.webcodein.workshop.strategy.domain.PaymentStrategy;
import com.webcodein.workshop.strategy.domain.PaymentType;
import jakarta.inject.Named;

import java.util.UUID;
import java.util.logging.Logger;

@Named("creditCardStrategy")
public class CreditCardPaymentStrategy implements PaymentStrategy {
    private static final Logger log = Logger.getLogger(CreditCardPaymentStrategy.class.getName());

    @Override
    public boolean supports(PaymentType type) {
        return type == PaymentType.CREDIT_CARD;
    }

    @Override
    public PaymentResult process(PaymentRequest request) {
        log.info("Processing Credit Card payment of $" + request.amount() + " for customer " + request.customerId());
        // Simulating 3rd party API call
        return new PaymentResult(true, UUID.randomUUID().toString(), "Credit Card payment successful");
    }
}
