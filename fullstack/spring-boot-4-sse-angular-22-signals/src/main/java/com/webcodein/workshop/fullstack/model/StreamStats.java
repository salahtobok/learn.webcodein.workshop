package com.webcodein.workshop.fullstack.model;

public record StreamStats(
        int activeEmitters,
        long totalEventsPushed,
        boolean virtualThreadsEnabled
) {
    public StreamStats {
        if (activeEmitters < 0) {
            throw new IllegalArgumentException("activeEmitters cannot be negative");
        }
        if (totalEventsPushed < 0) {
            throw new IllegalArgumentException("totalEventsPushed cannot be negative");
        }
    }
}
