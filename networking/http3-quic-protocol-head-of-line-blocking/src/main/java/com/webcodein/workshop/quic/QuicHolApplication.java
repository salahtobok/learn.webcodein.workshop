package com.webcodein.workshop.quic;

import com.webcodein.workshop.quic.model.BenchmarkResult;
import com.webcodein.workshop.quic.service.QuicBenchmarkService;

/**
 * Console application comparing TCP vs QUIC Head-of-Line blocking in Java 25.
 */
public class QuicHolApplication {

    public static void main(String[] args) {
        System.out.println("=== HTTP/3 QUIC vs. HTTP/2 TCP Head-of-Line Blocking Benchmark ===");
        QuicBenchmarkService benchmarkService = new QuicBenchmarkService();

        int streams = 8;
        int framesPerStream = 20;
        double lossRate = 0.10; // 10% packet drop

        BenchmarkResult tcpResult = benchmarkService.runTcpSimulation(streams, framesPerStream, lossRate);
        BenchmarkResult quicResult = benchmarkService.runQuicSimulation(streams, framesPerStream, lossRate);

        System.out.printf("Protocol: %s | Loss: %.1f%% | Duration: %.2f ms | HoL Stall: %.2f ms | Retransmits: %d%n",
            tcpResult.protocol(), tcpResult.lossRatePercent(), tcpResult.durationMillis(),
            tcpResult.holBlockedMillis(), tcpResult.retransmissions());

        System.out.printf("Protocol: %s | Loss: %.1f%% | Duration: %.2f ms | HoL Stall: %.2f ms | Retransmits: %d%n",
            quicResult.protocol(), quicResult.lossRatePercent(), quicResult.durationMillis(),
            quicResult.holBlockedMillis(), quicResult.retransmissions());

        double improvement = ((tcpResult.durationMillis() - quicResult.durationMillis()) / tcpResult.durationMillis()) * 100.0;
        System.out.printf("QUIC Performance Advantage: %.1f%% lower latency under %.0f%% packet loss.%n",
            improvement, lossRate * 100.0);
    }
}
