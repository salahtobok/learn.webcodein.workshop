package com.webcodein.workshop.realtime.protocol;

import com.webcodein.workshop.realtime.model.DeliveryGuarantee;
import com.webcodein.workshop.realtime.model.Directionality;
import com.webcodein.workshop.realtime.model.FrameResult;
import com.webcodein.workshop.realtime.model.MessagePayload;
import com.webcodein.workshop.realtime.model.TransportLayer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class ServerSentEventsProtocol implements RealTimeProtocol {

    private final TransportLayer transport;

    public ServerSentEventsProtocol() {
        this(TransportLayer.HTTP_2);
    }

    public ServerSentEventsProtocol(TransportLayer transport) {
        if (transport != TransportLayer.HTTP_1_1 && transport != TransportLayer.HTTP_2) {
            throw new IllegalArgumentException("SSE only operates on HTTP/1.1 or HTTP/2");
        }
        this.transport = transport;
    }

    @Override
    public String name() {
        return "Server-Sent Events (SSE)";
    }

    @Override
    public TransportLayer transport() {
        return transport;
    }

    @Override
    public Directionality directionality() {
        return Directionality.UNIDIRECTIONAL_SERVER_TO_CLIENT;
    }

    @Override
    public boolean supportsMultiplexing() {
        return transport == TransportLayer.HTTP_2;
    }

    @Override
    public boolean supportsDatagrams() {
        return false;
    }

    @Override
    public FrameResult transmit(MessagePayload payload, DeliveryGuarantee guarantee) {
        Objects.requireNonNull(payload, "payload must not be null");
        if (guarantee == DeliveryGuarantee.UNRELIABLE_UNORDERED) {
            throw new UnsupportedOperationException("SSE does not support unreliable unordered delivery");
        }

        long start = System.nanoTime();
        byte[] encodedData;
        int framingOverhead;

        if (payload.isBinary()) {
            // SSE is text-only. Binary payloads require Base64 encoding.
            String base64 = Base64.getEncoder().encodeToString(payload.data());
            encodedData = base64.getBytes(StandardCharsets.UTF_8);
        } else {
            encodedData = payload.data();
        }

        // Format: "id: <streamId>\nevent: message\ndata: " + body + "\n\n"
        String envelopePrefix = "id: " + payload.streamId() + "\nevent: message\ndata: ";
        String envelopeSuffix = "\n\n";
        byte[] prefixBytes = envelopePrefix.getBytes(StandardCharsets.UTF_8);
        byte[] suffixBytes = envelopeSuffix.getBytes(StandardCharsets.UTF_8);

        framingOverhead = prefixBytes.length + suffixBytes.length + (encodedData.length - payload.data().length);
        int totalBytes = prefixBytes.length + encodedData.length + suffixBytes.length;
        long duration = System.nanoTime() - start;

        return new FrameResult(
                name(),
                framingOverhead,
                payload.data().length,
                totalBytes,
                DeliveryGuarantee.RELIABLE_ORDERED,
                duration
        );
    }
}
