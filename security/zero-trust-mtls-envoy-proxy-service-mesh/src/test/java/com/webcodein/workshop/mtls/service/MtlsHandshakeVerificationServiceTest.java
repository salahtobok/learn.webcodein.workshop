package com.webcodein.workshop.mtls.service;

import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.MtlsAccessDecision;
import com.webcodein.workshop.mtls.model.SpiffeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MtlsHandshakeVerificationServiceTest {

    @Test
    @DisplayName("Should simulate end-to-end mTLS service mesh communication workflow")
    void shouldSimulateServiceMeshWorkflow() {
        MtlsHandshakeVerificationService mesh = new MtlsHandshakeVerificationService("cluster.local");

        CertificateIdentity orderIdentity = mesh.issueWorkloadIdentity("cluster.local", "prod", "order-service");
        CertificateIdentity frontendIdentity = mesh.issueWorkloadIdentity("cluster.local", "prod", "frontend-gateway");

        SpiffeId paymentTarget = new SpiffeId("cluster.local", "prod", "payment-service");
        SpiffeId orderTarget = new SpiffeId("cluster.local", "prod", "order-service");

        // order-service can call payment-service
        MtlsAccessDecision call1 = mesh.simulateMtlsCall(orderIdentity, paymentTarget);
        assertThat(call1.allowed()).isTrue();

        // frontend-gateway can call order-service
        MtlsAccessDecision call2 = mesh.simulateMtlsCall(frontendIdentity, orderTarget);
        assertThat(call2.allowed()).isTrue();

        // frontend-gateway CANNOT call payment-service directly
        MtlsAccessDecision call3 = mesh.simulateMtlsCall(frontendIdentity, paymentTarget);
        assertThat(call3.allowed()).isFalse();
    }
}
