package com.webcodein.workshop.quic.engine;

import com.webcodein.workshop.quic.model.Packet;
import com.webcodein.workshop.quic.model.StreamBuffer;
import com.webcodein.workshop.quic.model.StreamFrame;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulates TCP transport where streams share a single sequenced byte-stream pipeline.
 * Under TCP, if packet N is lost, subsequent packets N+1, N+2 cannot be delivered
 * to the application layer, stalling ALL streams (TCP Head-of-Line Blocking).
 */
public class TcpMultiplexedConnection {

    private final Map<Long, StreamBuffer> streams = new ConcurrentHashMap<>();
    private final Map<Long, Packet> outOfOrderTcpPackets = new TreeMap<>();
    private long nextExpectedTcpSeq = 0L;
    private long totalBytesDelivered = 0L;
    private long totalHolBlockedNanos = 0L;
    private int retransmissions = 0;

    public void registerStream(long streamId) {
        streams.put(streamId, new StreamBuffer(streamId));
    }

    public synchronized void receivePacket(Packet packet, boolean isDropped, long rttNanos) {
        if (isDropped) {
            // TCP segment dropped: Requires simulated retransmission after 1 RTT
            retransmissions++;
            // During retransmission delay, application is completely blocked
            totalHolBlockedNanos += rttNanos;
            return;
        }

        // Segment received
        if (packet.packetNumber() == nextExpectedTcpSeq) {
            deliverPacketFrames(packet);
            nextExpectedTcpSeq++;

            // Process any buffered segments that arrived out of order
            while (outOfOrderTcpPackets.containsKey(nextExpectedTcpSeq)) {
                Packet nextPacket = outOfOrderTcpPackets.remove(nextExpectedTcpSeq);
                deliverPacketFrames(nextPacket);
                nextExpectedTcpSeq++;
            }
        } else if (packet.packetNumber() > nextExpectedTcpSeq) {
            // Out-of-order segment: Buffer at TCP socket layer, cannot deliver to any stream!
            outOfOrderTcpPackets.put(packet.packetNumber(), packet);
            // Any stream inside this packet is stalled waiting for nextExpectedTcpSeq
            totalHolBlockedNanos += (rttNanos / 2);
        }
    }

    private void deliverPacketFrames(Packet packet) {
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
}
