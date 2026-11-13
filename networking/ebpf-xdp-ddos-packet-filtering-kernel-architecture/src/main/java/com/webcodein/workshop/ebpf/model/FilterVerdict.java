package com.webcodein.workshop.ebpf.model;

import java.util.Objects;

public record FilterVerdict(
        XdpAction action,
        String ruleMatched,
        long processingLatencyNanos
) {
    public FilterVerdict {
        Objects.requireNonNull(action, "action must not be null");
        Objects.requireNonNull(ruleMatched, "ruleMatched must not be null");
    }

    public boolean isDropped() {
        return action == XdpAction.XDP_DROP;
    }

    public boolean isPassed() {
        return action == XdpAction.XDP_PASS;
    }
}
