package com.webcodein.workshop.resilience.service;

import com.webcodein.workshop.resilience.model.PaymentRequest;
import com.webcodein.workshop.resilience.model.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class MockExternalPaymentGateway implements ExternalPaymentGateway {

    private final AtomicBoolean simulateFailure = new AtomicBoolean(false);

    @Override
    public PaymentResponse processRemotePayment(PaymentRequest request) {
        if (simulateFailure.get() || request.transactionId().startsWith("FAIL_")) {
            throw new RemoteGatewayException("Remote payment gateway returned HTTP 503 Service Unavailable");
        }
        return PaymentResponse.success(request.transactionId(), request.amount(), request.currency());
    }

    @Override
    public void setSimulateFailure(boolean simulate) {
        this.simulateFailure.set(simulate);
    }

    @Override
    public boolean isSimulateFailure() {
        return this.simulateFailure.get();
    }

    public static class RemoteGatewayException extends RuntimeException {
        public RemoteGatewayException(String message) {
            super(message);
        }
    }
}
