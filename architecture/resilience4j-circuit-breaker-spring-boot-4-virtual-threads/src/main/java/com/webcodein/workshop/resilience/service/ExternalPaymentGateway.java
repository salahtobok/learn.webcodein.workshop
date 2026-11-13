package com.webcodein.workshop.resilience.service;

import com.webcodein.workshop.resilience.model.PaymentRequest;
import com.webcodein.workshop.resilience.model.PaymentResponse;

public interface ExternalPaymentGateway {
    PaymentResponse processRemotePayment(PaymentRequest request);
    void setSimulateFailure(boolean simulateFailure);
    boolean isSimulateFailure();
}
