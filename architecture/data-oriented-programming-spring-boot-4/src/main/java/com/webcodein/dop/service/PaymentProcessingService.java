package com.webcodein.dop.service;

import com.webcodein.dop.model.*;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessingService {

    public String processPayment(PaymentRequest request) {
        // Data-Oriented Programming using Pattern Matching for switch (Java 25)
        return switch (request.method()) {
            case CreditCard cc -> "Processing credit card ending in " + cc.cardNumber().substring(cc.cardNumber().length() - 4) + " for $" + request.amount();
            case PayPal pp -> "Processing PayPal payment for " + pp.email() + " for $" + request.amount();
            case Crypto cr when cr.currency().equals("BTC") -> "Processing Bitcoin payment to " + cr.walletAddress() + " for $" + request.amount();
            case Crypto cr -> "Processing Crypto payment in " + cr.currency() + " to " + cr.walletAddress() + " for $" + request.amount();
        };
    }
}
