package com.webcodein.workshop.quic.engine;

import com.webcodein.workshop.quic.model.Packet;
import com.webcodein.workshop.quic.model.StreamBuffer;
import com.webcodein.workshop.quic.model.StreamFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulates QUIC transport over UDP.
 * QUIC separates packet sequence numbers from stream byte offsets.
 * Even if a packet carrying Stream A is dropped, packets carrying Stream B
 * deliver their frames to user-space immediately without any Head-of-Line blocking.
 */
public class QuicMultiplexedConnection {

    private final Map<Long, StreamBuffer> streams = new ConcurrentHashMap<>();
    private long totalBytesDelivered = 0L;
    private long totalHolBlockedNanos = 0L;
    private int retransmissions = 0;

    public void registerStream(long streamId) {
        streams.put(streamId, new StreamBuffer(streamId));
    }

    public synchronized void receivePacket(Packet packet, boolean isDropped, long rttNanos) {
        if (isDropped) {
            // QUIC packet dropped: Only the specific stream frames inside this packet
            // will require retransmission. Unaffected streams continue without blocking!
            retransmissions++;
            // Only stream-specific stall occurs if that exact stream had pending reads
            for (StreamFrame frame : packet.frames()) {
                // In QUIC, only the stream with the gap is delayed, not other streams
                totalHolBlockedNanos += (rttNanos / Math.max(1, streams.size()));
            }
            return;
        }

        // Delivered directly to each independent stream buffer immediately!
        for (StreamFrame frame : packet.frames()) {
            StreamBuffer buffer = streams.computeIfAbsent(frame.streamId(), StreamBuffer::new);
            buffer.pushFrame(frame);
            byte[] delivered = buffer.drainContiguousBytes();
            totalBytesDelivered += delivered.length;
        }
    }

    public boolean areAllStreamsFinished() {
        return !streams.isEmpty() && streams.values().stream().allMatch(StreamBuffer::isStreamFinished);
    }

    public long totalBytesDelivered() {
        return totalBytesDelivered;
    }

    public long totalHolBlockedNanos() {
        return totalHolBlockedNanos;
    }

    public int retransmissions() {
        return retransmissions;
    }

    public StreamBuffer getStreamBuffer(long streamId) {
        return streams.get(streamId);
    }
}
