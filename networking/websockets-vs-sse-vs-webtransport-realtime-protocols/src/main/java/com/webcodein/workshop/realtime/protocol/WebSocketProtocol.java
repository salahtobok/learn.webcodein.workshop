package com.webcodein.workshop.realtime.protocol;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.Directionality;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.TransportLayer;

import java.util.Objects;

public class WebSocketProtocol implements RealTimeProtocol {

    private final boolean clientSideMasking;

    public WebSocketProtocol() {
        this(false); // Server-to-client default
    }

    public WebSocketProtocol(boolean clientSideMasking) {
        this.clientSideMasking = clientSideMasking;
    }

    @Override
    public String name() {
        return "WebSocket (RFC 6455)";
    }

    @Override
    public TransportLayer transport() {
        return TransportLayer.TCP;
    }

    @Override
    public Directionality directionality() {
        return Directionality.BIDIRECTIONAL;
    }

    @Override
    public boolean supportsMultiplexing() {
        return false; // WebSocket does not natively multiplex multiple logical streams over one connection
    }

    @Override
    public boolean supportsDatagrams() {
        return false; // WebSocket is strictly TCP-backed reliable byte stream
    }

    @Override
    public FrameResult transmit(MessagePayload payload, DeliveryGuarantee guarantee) {
        Objects.requireNonNull(payload, "payload must not be null");
        if (guarantee == DeliveryGuarantee.UNRELIABLE_UNORDERED) {
            throw new UnsupportedOperationException("WebSocket does not support unreliable unordered delivery");
        }

        long start = System.nanoTime();
        int payloadLen = payload.data().length;

        // RFC 6455 Header calculation:
        // 1 byte: FIN + Opcode
        // 1 byte: Mask bit + 7-bit length
        int headerBytes = 2;

        if (payloadLen > 125 && payloadLen <= 65535) {
            headerBytes += 2; // 16-bit extended length
        } else if (payloadLen > 65535) {
            headerBytes += 8; // 64-bit extended length
        }

        if (clientSideMasking) {
            headerBytes += 4; // 4-byte masking key
        }

        int totalBytes = headerBytes + payloadLen;
        long duration = System.nanoTime() - start;

        return new FrameResult(
                name(),
                headerBytes,
                payloadLen,
                totalBytes,
                DeliveryGuarantee.RELIABLE_ORDERED,
                duration
        );
    }
}
