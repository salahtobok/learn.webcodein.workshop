package com.webcodein.workshop.realtime.protocol;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.Directionality;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.TransportLayer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebTransportProtocolTest {

    @Test
    @DisplayName("Should verify WebTransport protocol attributes")
    void shouldVerifyWebTransportProtocolAttributes() {
        WebTransportProtocol wt = new WebTransportProtocol();
        assertThat(wt.name()).isEqualTo("WebTransport (HTTP/3 QUIC)");
        assertThat(wt.transport()).isEqualTo(TransportLayer.HTTP_3_QUIC);
        assertThat(wt.directionality()).isEqualTo(Directionality.BIDIRECTIONAL);
        assertThat(wt.supportsMultiplexing()).isTrue();
        assertThat(wt.supportsDatagrams()).isTrue();
    }

    @Test
    @DisplayName("Should transmit reliable stream frame")
    void shouldTransmitReliableStreamFrame() {
        WebTransportProtocol wt = new WebTransportProtocol();
        MessagePayload payload = MessagePayload.text("stream-1", "reliable payload");

        FrameResult result = wt.transmit(payload, DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(result.deliveryGuarantee()).isEqualTo(DeliveryGuarantee.RELIABLE_ORDERED);
        assertThat(result.frameHeaderSizeBytes()).isEqualTo(4);
        assertThat(result.totalBytesTransmitted()).isEqualTo(4 + payload.data().length);
    }

    @Test
    @DisplayName("Should transmit unreliable datagram frame with minimum overhead")
    void shouldTransmitUnreliableDatagramFrame() {
        WebTransportProtocol wt = new WebTransportProtocol();
        MessagePayload payload = MessagePayload.binary("datagram-stream", new byte[]{1, 2, 3});

        FrameResult result = wt.transmit(payload, DeliveryGuarantee.UNRELIABLE_UNORDERED);
        assertThat(result.deliveryGuarantee()).isEqualTo(DeliveryGuarantee.UNRELIABLE_UNORDERED);
        assertThat(result.frameHeaderSizeBytes()).isEqualTo(2);
        assertThat(result.totalBytesTransmitted()).isEqualTo(5);
    }
}
