package com.webcodein.testing.tdd.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(String reference, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (paymentRepository.findByReference(reference).isPresent()) {
            throw new IllegalStateException("Payment reference already exists");
        }

        Payment payment = new Payment(UUID.randomUUID(), reference, amount, "PROCESSED");
        return paymentRepository.save(payment);
    }
}
