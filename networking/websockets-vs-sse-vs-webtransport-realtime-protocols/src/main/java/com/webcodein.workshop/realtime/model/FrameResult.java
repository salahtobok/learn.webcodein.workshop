package com.webcodein.workshop.realtime.model;

import java.util.Objects;

public record FrameResult(
        String protocolName,
        int frameHeaderSizeBytes,
        int payloadSizeBytes,
        int totalBytesTransmitted,
        DeliveryGuarantee deliveryGuarantee,
        long latencyNanos
) {
    public FrameResult {
        Objects.requireNonNull(protocolName, "protocolName must not be null");
        Objects.requireNonNull(deliveryGuarantee, "deliveryGuarantee must not be null");
        if (frameHeaderSizeBytes < 0 || payloadSizeBytes < 0 || totalBytesTransmitted < 0) {
            throw new IllegalArgumentException("Byte counts must not be negative");
        }
    }

    public double overheadRatio() {
        if (totalBytesTransmitted == 0) return 0.0;
        return (double) frameHeaderSizeBytes / totalBytesTransmitted;
    }
}
