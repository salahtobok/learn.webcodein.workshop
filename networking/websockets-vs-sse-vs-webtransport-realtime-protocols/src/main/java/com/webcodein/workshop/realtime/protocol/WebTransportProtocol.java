package com.webcodein.workshop.realtime.protocol;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.Directionality;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.TransportLayer;

import java.util.Objects;

public class WebTransportProtocol implements RealTimeProtocol {

    public WebTransportProtocol() {}

    @Override
    public String name() {
        return "WebTransport (HTTP/3 QUIC)";
    }

    @Override
    public TransportLayer transport() {
        return TransportLayer.HTTP_3_QUIC;
    }

    @Override
    public Directionality directionality() {
        return Directionality.BIDIRECTIONAL;
    }

    @Override
    public boolean supportsMultiplexing() {
        return true; // Native QUIC stream multiplexing without Head-of-Line blocking
    }

    @Override
    public boolean supportsDatagrams() {
        return true; // Native RFC 9221 QUIC datagrams support
    }

    @Override
    public FrameResult transmit(MessagePayload payload, DeliveryGuarantee guarantee) {
        Objects.requireNonNull(payload, "payload must not be null");
        Objects.requireNonNull(guarantee, "guarantee must not be null");

        long start = System.nanoTime();
        int payloadLen = payload.data().length;
        int headerBytes;

        if (guarantee == DeliveryGuarantee.UNRELIABLE_UNORDERED) {
            // WebTransport Datagram Frame (RFC 9221):
            // Frame Type (0x30/0x31) + Session ID varint + Payload
            headerBytes = 2; // Lightweight datagram envelope
        } else {
            // WebTransport Stream Frame (Unidirectional or Bidirectional QUIC stream):
            // Frame Type (0x08-0x0f) + Stream ID varint + Offset varint + Length varint
            headerBytes = 4;
        }

        int totalBytes = headerBytes + payloadLen;
        long duration = System.nanoTime() - start;

        return new FrameResult(
                name(),
                headerBytes,
                payloadLen,
                totalBytes,
                guarantee,
                duration
        );
    }
}
