package com.webcodein.concurrency.exception;

public class FraudDetectedException extends RuntimeException {
    private final int riskScore;

    public FraudDetectedException(String message, int riskScore) {
        super(message);
        this.riskScore = riskScore;
    }

    public int getRiskScore() {
        return riskScore;
    }
}
