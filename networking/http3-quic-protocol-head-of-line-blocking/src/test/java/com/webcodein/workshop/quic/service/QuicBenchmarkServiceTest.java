package com.webcodein.workshop.quic.service;

import com.webcodein.workshop.quic.model.BenchmarkResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuicBenchmarkServiceTest {

    @Test
    @DisplayName("QUIC simulation should have lower Head-of-Line blocking duration than TCP under packet loss")
    void shouldShowQuicOutperformingTcpUnderLoss() {
        QuicBenchmarkService service = new QuicBenchmarkService();

        int streamCount = 6;
        int framesPerStream = 15;
        double lossRate = 0.15; // 15% packet loss

        BenchmarkResult tcpResult = service.runTcpSimulation(streamCount, framesPerStream, lossRate);
        BenchmarkResult quicResult = service.runQuicSimulation(streamCount, framesPerStream, lossRate);

        assertThat(tcpResult.protocol()).isEqualTo("TCP/HTTP-2");
        assertThat(quicResult.protocol()).isEqualTo("QUIC/HTTP-3");
        assertThat(tcpResult.totalBytesTransferred()).isEqualTo(quicResult.totalBytesTransferred());

        // QUIC HoL blocked time must be significantly lower than TCP
        assertThat(quicResult.holBlockedNanos()).isLessThan(tcpResult.holBlockedNanos());
        assertThat(quicResult.durationMillis()).isLessThan(tcpResult.durationMillis());
    }
}
