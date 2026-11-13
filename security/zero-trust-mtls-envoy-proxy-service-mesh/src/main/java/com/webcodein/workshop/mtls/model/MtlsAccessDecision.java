package com.webcodein.workshop.mtls.model;

/**
 * Access decision evaluated across mTLS peer identities.
 */
public record MtlsAccessDecision(boolean allowed, String reason, String sourceSpiffeId, String targetSpiffeId) {

    public static MtlsAccessDecision allow(String src, String target) {
        return new MtlsAccessDecision(true, "Authorized by service mesh mTLS policy", src, target);
    }

    public static MtlsAccessDecision deny(String reason, String src, String target) {
        return new MtlsAccessDecision(false, reason, src, target);
    }
}
