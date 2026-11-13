package com.webcodein.workshop.quic.service;

import com.webcodein.workshop.quic.engine.PacketLossSimulator;
import com.webcodein.workshop.quic.engine.QuicMultiplexedConnection;
import com.webcodein.workshop.quic.engine.TcpMultiplexedConnection;
import com.webcodein.workshop.quic.model.BenchmarkResult;
import com.webcodein.workshop.quic.model.Packet;
import com.webcodein.workshop.quic.model.StreamFrame;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes comparative simulations between TCP and QUIC under synthetic network loss.
 */
public class QuicBenchmarkService {

    private static final long SIMULATED_RTT_NANOS = 50_000_000L; // 50ms RTT

    public BenchmarkResult runTcpSimulation(int streamCount, int framesPerStream, double lossProbability) {
        TcpMultiplexedConnection connection = new TcpMultiplexedConnection();
        for (long s = 1; s <= streamCount; s++) {
            connection.registerStream(s);
        }

        List<Packet> packets = generateTraffic(streamCount, framesPerStream);
        PacketLossSimulator lossSim = new PacketLossSimulator(lossProbability, 42L);

        long startNanos = System.nanoTime();
        long totalBytes = 0;

        for (Packet packet : packets) {
            boolean dropped = lossSim.shouldDrop();
            connection.receivePacket(packet, dropped, SIMULATED_RTT_NANOS);
            if (dropped) {
                // Retransmit dropped packet after RTT
                connection.receivePacket(packet, false, SIMULATED_RTT_NANOS);
            }
            totalBytes += packet.frames().stream().mapToInt(StreamFrame::length).sum();
        }

        long durationNanos = (System.nanoTime() - startNanos) + connection.totalHolBlockedNanos();

        return new BenchmarkResult(
            "TCP/HTTP-2",
            streamCount,
            lossProbability * 100.0,
            totalBytes,
            durationNanos,
            connection.totalHolBlockedNanos(),
            connection.retransmissions()
        );
    }

    public BenchmarkResult runQuicSimulation(int streamCount, int framesPerStream, double lossProbability) {
        QuicMultiplexedConnection connection = new QuicMultiplexedConnection();
        for (long s = 1; s <= streamCount; s++) {
            connection.registerStream(s);
        }

        List<Packet> packets = generateTraffic(streamCount, framesPerStream);
        PacketLossSimulator lossSim = new PacketLossSimulator(lossProbability, 42L);

        long startNanos = System.nanoTime();
        long totalBytes = 0;

        for (Packet packet : packets) {
            boolean dropped = lossSim.shouldDrop();
            connection.receivePacket(packet, dropped, SIMULATED_RTT_NANOS);
            if (dropped) {
                // Retransmit dropped packet after RTT
                connection.receivePacket(packet, false, SIMULATED_RTT_NANOS);
            }
            totalBytes += packet.frames().stream().mapToInt(StreamFrame::length).sum();
        }

        long durationNanos = (System.nanoTime() - startNanos) + connection.totalHolBlockedNanos();

        return new BenchmarkResult(
            "QUIC/HTTP-3",
            streamCount,
            lossProbability * 100.0,
            totalBytes,
            durationNanos,
            connection.totalHolBlockedNanos(),
            connection.retransmissions()
        );
    }

    private List<Packet> generateTraffic(int streamCount, int framesPerStream) {
        List<Packet> packets = new ArrayList<>();
        long packetNumber = 0;

        // Interleave frames across streams to model multiplexing
        for (int frameIdx = 0; frameIdx < framesPerStream; frameIdx++) {
            for (long streamId = 1; streamId <= streamCount; streamId++) {
                byte[] payload = ("Payload_S" + streamId + "_F" + frameIdx).getBytes(StandardCharsets.UTF_8);
                boolean isFin = (frameIdx == framesPerStream - 1);
                long offset = (long) frameIdx * payload.length;

                StreamFrame frame = new StreamFrame(streamId, offset, payload, isFin);
                Packet packet = new Packet(packetNumber++, List.of(frame), System.nanoTime());
                packets.add(packet);
            }
        }
        return packets;
    }
}
