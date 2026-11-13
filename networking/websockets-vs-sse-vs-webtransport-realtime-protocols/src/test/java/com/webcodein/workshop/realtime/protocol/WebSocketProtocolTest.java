package com.webcodein.workshop.realtime.protocol;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.Directionality;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.TransportLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebSocketProtocolTest {

    @Test
    @DisplayName("Should verify WebSocket protocol attributes")
    void shouldVerifyWebSocketProtocolAttributes() {
        WebSocketProtocol ws = new WebSocketProtocol();
        assertThat(ws.name()).isEqualTo("WebSocket (RFC 6455)");
        assertThat(ws.transport()).isEqualTo(TransportLayer.TCP);
        assertThat(ws.directionality()).isEqualTo(Directionality.BIDIRECTIONAL);
        assertThat(ws.supportsMultiplexing()).isFalse();
        assertThat(ws.supportsDatagrams()).isFalse();
    }

    @Test
    @DisplayName("Should calculate small frame header without masking")
    void shouldCalculateSmallFrameHeaderWithoutMasking() {
        WebSocketProtocol ws = new WebSocketProtocol(false);
        MessagePayload payload = MessagePayload.text("s1", "short text");

        FrameResult result = ws.transmit(payload, DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(result.frameHeaderSizeBytes()).isEqualTo(2);
        assertThat(result.totalBytesTransmitted()).isEqualTo(2 + payload.data().length);
    }

    @Test
    @DisplayName("Should include masking key for client-side frames")
    void shouldIncludeMaskingKeyForClientSideFrames() {
        WebSocketProtocol ws = new WebSocketProtocol(true);
        MessagePayload payload = MessagePayload.text("s1", "short text");

        FrameResult result = ws.transmit(payload, DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(result.frameHeaderSizeBytes()).isEqualTo(6); // 2 bytes + 4 bytes mask
    }

    @Test
    @DisplayName("Should calculate 16-bit extended header for larger payloads")
    void shouldCalculate16BitExtendedHeaderForLargerPayloads() {
        WebSocketProtocol ws = new WebSocketProtocol(false);
        byte[] medium = new byte[1000];
        MessagePayload payload = MessagePayload.binary("s1", medium);

        FrameResult result = ws.transmit(payload, DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(result.frameHeaderSizeBytes()).isEqualTo(4); // 2 bytes base + 2 bytes length
    }

    @Test
    @DisplayName("Should reject unreliable delivery")
    void shouldRejectUnreliableDelivery() {
        WebSocketProtocol ws = new WebSocketProtocol();
        MessagePayload payload = MessagePayload.text("s1", "test");

        assertThatThrownBy(() -> ws.transmit(payload, DeliveryGuarantee.UNRELIABLE_UNORDERED))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
