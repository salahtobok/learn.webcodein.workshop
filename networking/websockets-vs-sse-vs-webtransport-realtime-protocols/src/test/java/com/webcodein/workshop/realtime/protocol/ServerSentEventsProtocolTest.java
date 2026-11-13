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

class ServerSentEventsProtocolTest {

    @Test
    @DisplayName("Should verify protocol attributes")
    void shouldVerifyProtocolAttributes() {
        ServerSentEventsProtocol sse = new ServerSentEventsProtocol(TransportLayer.HTTP_2);
        assertThat(sse.name()).isEqualTo("Server-Sent Events (SSE)");
        assertThat(sse.transport()).isEqualTo(TransportLayer.HTTP_2);
        assertThat(sse.directionality()).isEqualTo(Directionality.UNIDIRECTIONAL_SERVER_TO_CLIENT);
        assertThat(sse.supportsMultiplexing()).isTrue();
        assertThat(sse.supportsDatagrams()).isFalse();
    }

    @Test
    @DisplayName("Should frame text payload with SSE envelope")
    void shouldFrameTextPayloadWithSseEnvelope() {
        ServerSentEventsProtocol sse = new ServerSentEventsProtocol();
        MessagePayload payload = MessagePayload.text("stream-1", "ticker:AAPL=185.50");

        FrameResult result = sse.transmit(payload, DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(result.payloadSizeBytes()).isEqualTo(payload.data().length);
        assertThat(result.frameHeaderSizeBytes()).isGreaterThan(0);
        assertThat(result.totalBytesTransmitted()).isEqualTo(result.payloadSizeBytes() + result.frameHeaderSizeBytes());
    }

    @Test
    @DisplayName("Should encode binary payload via Base64")
    void shouldEncodeBinaryPayloadViaBase64() {
        ServerSentEventsProtocol sse = new ServerSentEventsProtocol();
        byte[] binaryData = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        MessagePayload payload = MessagePayload.binary("stream-2", binaryData);

        FrameResult result = sse.transmit(payload, DeliveryGuarantee.RELIABLE_ORDERED);
        // Base64 expansion adds overhead
        assertThat(result.totalBytesTransmitted()).isGreaterThan(binaryData.length);
    }

    @Test
    @DisplayName("Should reject unreliable delivery guarantee")
    void shouldRejectUnreliableDeliveryGuarantee() {
        ServerSentEventsProtocol sse = new ServerSentEventsProtocol();
        MessagePayload payload = MessagePayload.text("stream-1", "test");

        assertThatThrownBy(() -> sse.transmit(payload, DeliveryGuarantee.UNRELIABLE_UNORDERED))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
