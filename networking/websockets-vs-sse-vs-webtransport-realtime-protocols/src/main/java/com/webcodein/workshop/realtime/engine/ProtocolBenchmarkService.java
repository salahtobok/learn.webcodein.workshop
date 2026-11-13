package com.webcodein.workshop.realtime.engine;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.ProtocolMetrics;
import com.webcodein.workshop.realtime.protocol.RealTimeProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProtocolBenchmarkService {

    public ProtocolMetrics benchmarkThroughput(
            RealTimeProtocol protocol,
            List<MessagePayload> payloads,
            DeliveryGuarantee guarantee
    ) {
        Objects.requireNonNull(protocol, "protocol must not be null");
        Objects.requireNonNull(payloads, "payloads must not be null");

        int totalFrames = 0;
        long totalBytes = 0;
        long totalHeaderBytes = 0;
        long totalDurationNanos = 0;

        for (MessagePayload payload : payloads) {
            FrameResult result = protocol.transmit(payload, guarantee);
            totalFrames++;
            totalBytes += result.totalBytesTransmitted();
            totalHeaderBytes += result.frameHeaderSizeBytes();
            totalDurationNanos += result.latencyNanos();
        }

        double avgLatencyMs = totalFrames > 0 ? (totalDurationNanos / 1_000_000.0) / totalFrames : 0.0;
        double framingOverheadRatio = totalBytes > 0 ? (double) totalHeaderBytes / totalBytes : 0.0;

        return new ProtocolMetrics(
                protocol.name(),
                totalFrames,
                totalBytes,
                avgLatencyMs,
                framingOverheadRatio
        );
    }

    public List<FrameResult> simulateBurstTransmission(
            RealTimeProtocol protocol,
            int count,
            int payloadSize,
            boolean isBinary,
            DeliveryGuarantee guarantee
    ) {
        List<FrameResult> results = new ArrayList<>();
        byte[] dummyBytes = new byte[payloadSize];

        for (int i = 0; i < count; i++) {
            MessagePayload payload = new MessagePayload("stream-" + (i % 4), dummyBytes, isBinary, System.currentTimeMillis());
            results.add(protocol.transmit(payload, guarantee));
        }
        return results;
    }
}
