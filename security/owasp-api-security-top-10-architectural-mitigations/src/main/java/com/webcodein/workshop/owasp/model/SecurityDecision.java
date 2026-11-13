package com.webcodein.workshop.owasp.model;

/**
 * Access control decision evaluation result.
 */
public record SecurityDecision(boolean allowed, String violationType, String reason) {

    public static SecurityDecision allow() {
        return new SecurityDecision(true, null, null);
    }

    public static SecurityDecision deny(String violationType, String reason) {
        return new SecurityDecision(false, violationType, reason);
    }
}
