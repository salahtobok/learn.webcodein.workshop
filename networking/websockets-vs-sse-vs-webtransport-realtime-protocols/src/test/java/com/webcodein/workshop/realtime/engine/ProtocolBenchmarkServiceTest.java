package com.webcodein.workshop.realtime.engine;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.ProtocolMetrics;
import com.webcodein.workshop.realtime.protocol.ServerSentEventsProtocol;
import com.webcodein.workshop.realtime.protocol.WebSocketProtocol;
import com.webcodein.workshop.realtime.protocol.WebTransportProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProtocolBenchmarkServiceTest {

    private final ProtocolBenchmarkService benchmarkService = new ProtocolBenchmarkService();

    @Test
    @DisplayName("Should benchmark throughput and calculate overhead correctly")
    void shouldBenchmarkThroughputAndCalculateOverhead() {
        WebSocketProtocol ws = new WebSocketProtocol();
        List<MessagePayload> payloads = List.of(
                MessagePayload.text("s1", "event A"),
                MessagePayload.text("s1", "event B"),
                MessagePayload.text("s1", "event C")
        );

        ProtocolMetrics metrics = benchmarkService.benchmarkThroughput(ws, payloads, DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(metrics.totalFrames()).isEqualTo(3);
        assertThat(metrics.framingOverheadRatio()).isGreaterThan(0.0).isLessThan(1.0);
    }

    @Test
    @DisplayName("Should compare burst transmission between SSE, WebSocket, and WebTransport")
    void shouldCompareBurstTransmission() {
        ServerSentEventsProtocol sse = new ServerSentEventsProtocol();
        WebSocketProtocol ws = new WebSocketProtocol();
        WebTransportProtocol wt = new WebTransportProtocol();

        List<FrameResult> sseFrames = benchmarkService.simulateBurstTransmission(sse, 10, 128, false, DeliveryGuarantee.RELIABLE_ORDERED);
        List<FrameResult> wsFrames = benchmarkService.simulateBurstTransmission(ws, 10, 128, false, DeliveryGuarantee.RELIABLE_ORDERED);
        List<FrameResult> wtFrames = benchmarkService.simulateBurstTransmission(wt, 10, 128, false, DeliveryGuarantee.RELIABLE_ORDERED);

        assertThat(sseFrames).hasSize(10);
        assertThat(wsFrames).hasSize(10);
        assertThat(wtFrames).hasSize(10);

        // WebSockets framing header (2 bytes) is more compact than text SSE envelope
        assertThat(wsFrames.getFirst().frameHeaderSizeBytes()).isLessThan(sseFrames.getFirst().frameHeaderSizeBytes());
    }
}
