package com.webcodein.workshop.mtls;

import com.webcodein.workshop.mtls.model.CertificateIdentity;
import com.webcodein.workshop.mtls.model.MtlsAccessDecision;
import com.webcodein.workshop.mtls.model.SpiffeId;
import com.webcodein.workshop.mtls.service.MtlsHandshakeVerificationService;

/**
 * Console application demonstrating Zero-Trust mTLS with SPIFFE IDs in Java 25.
 */
public class MtlsMeshApplication {

    public static void main(String[] args) {
        System.out.println("=== Zero-Trust mTLS & SPIFFE Identity Mesh Demonstration ===");
        String trustDomain = "cluster.local";
        MtlsHandshakeVerificationService meshService = new MtlsHandshakeVerificationService(trustDomain);

        // Issue SPIFFE identities
        CertificateIdentity orderServiceCert = meshService.issueWorkloadIdentity(trustDomain, "prod", "order-service");
        CertificateIdentity frontendCert = meshService.issueWorkloadIdentity(trustDomain, "prod", "frontend-gateway");
        CertificateIdentity rogueCert = meshService.issueWorkloadIdentity("foreign.domain", "default", "rogue-actor");

        SpiffeId paymentServiceId = new SpiffeId(trustDomain, "prod", "payment-service");

        // 1. Authorized Call: order-service -> payment-service
        System.out.println("\n1. mTLS Call: order-service -> payment-service:");
        MtlsAccessDecision decision1 = meshService.simulateMtlsCall(orderServiceCert, paymentServiceId);
        System.out.printf("   [DECISION] Allowed: %b | Reason: %s%n", decision1.allowed(), decision1.reason());

        // 2. Unauthorized Direct Call: frontend-gateway -> payment-service
        System.out.println("\n2. mTLS Call: frontend-gateway -> payment-service (Direct access prohibited):");
        MtlsAccessDecision decision2 = meshService.simulateMtlsCall(frontendCert, paymentServiceId);
        System.out.printf("   [DECISION] Allowed: %b | Reason: %s%n", decision2.allowed(), decision2.reason());

        // 3. Foreign Trust Domain Call: rogue-actor -> payment-service
        System.out.println("\n3. mTLS Call: rogue-actor -> payment-service (Trust domain mismatch):");
        MtlsAccessDecision decision3 = meshService.simulateMtlsCall(rogueCert, paymentServiceId);
        System.out.printf("   [DECISION] Allowed: %b | Reason: %s%n", decision3.allowed(), decision3.reason());
    }
}
