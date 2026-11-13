package com.webcodein.workshop.quic.engine;

import com.webcodein.workshop.quic.model.Packet;
import com.webcodein.workshop.quic.model.StreamFrame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TcpMultiplexedConnectionTest {

    @Test
    @DisplayName("Under TCP, a dropped packet blocks subsequent packets from delivering bytes")
    void shouldDemonstrateTcpHeadOfLineBlocking() {
        TcpMultiplexedConnection conn = new TcpMultiplexedConnection();
        conn.registerStream(1L);
        conn.registerStream(2L);

        StreamFrame frameStream1 = new StreamFrame(1L, 0L, "S1_DATA".getBytes(StandardCharsets.UTF_8), true);
        StreamFrame frameStream2 = new StreamFrame(2L, 0L, "S2_DATA".getBytes(StandardCharsets.UTF_8), true);

        Packet packet0 = new Packet(0L, List.of(frameStream1), 1000L);
        Packet packet1 = new Packet(1L, List.of(frameStream2), 2000L);

        // Packet 0 is dropped
        conn.receivePacket(packet0, true, 50_000_000L);
        // Packet 1 (Stream 2) arrives successfully
        conn.receivePacket(packet1, false, 50_000_000L);

        // TCP has delivered 0 bytes because packet 0 was missing! Stream 2 is blocked by Stream 1!
        assertThat(conn.totalBytesDelivered()).isEqualTo(0L);
        assertThat(conn.totalHolBlockedNanos()).isGreaterThan(0L);
        assertThat(conn.retransmissions()).isEqualTo(1);

        // Retransmission of packet 0 arrives
        conn.receivePacket(packet0, false, 50_000_000L);

        // Now both Stream 1 and Stream 2 are delivered
        assertThat(conn.totalBytesDelivered()).isEqualTo("S1_DATA".length() + "S2_DATA".length());
        assertThat(conn.areAllStreamsFinished()).isTrue();
    }
}
