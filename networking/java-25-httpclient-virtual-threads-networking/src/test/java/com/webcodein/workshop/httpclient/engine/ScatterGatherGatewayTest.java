package com.webcodein.workshop.httpclient.engine;

import com.sun.net.httpserver.HttpServer;
import com.webcodein.workshop.httpclient.model.DownstreamTarget;
import com.webcodein.workshop.httpclient.model.GatewaySummary;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScatterGatherGatewayTest {

    private static HttpServer server;
    private static int port;
    private final ScatterGatherGateway gateway = new ScatterGatherGateway();

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        server.createContext("/user-profile", exchange -> {
            try {
                Thread.sleep(50); // Simulate network latency
            } catch (InterruptedException ignored) {}
            byte[] response = "{\"id\":\"usr_100\",\"name\":\"Salah\"}".getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.createContext("/order-history", exchange -> {
            try {
                Thread.sleep(60); // Simulate network latency
            } catch (InterruptedException ignored) {}
            byte[] response = "[{\"orderId\":\"ord_501\",\"amount\":129.99}]".getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.createContext("/recommendations", exchange -> {
            byte[] response = "{\"items\":[\"item_A\",\"item_B\"]}".getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.start();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Should execute concurrent scatter-gather across multiple endpoints using virtual threads")
    void shouldExecuteConcurrentScatterGather() {
        String baseUrl = "http://localhost:" + port;
        List<DownstreamTarget> targets = List.of(
                DownstreamTarget.of("UserService", baseUrl + "/user-profile", Duration.ofSeconds(2)),
                DownstreamTarget.of("OrderService", baseUrl + "/order-history", Duration.ofSeconds(2)),
                DownstreamTarget.of("RecommendationService", baseUrl + "/recommendations", Duration.ofSeconds(2))
        );

        GatewaySummary summary = gateway.executeScatterGather(targets);

        assertThat(summary.allSucceeded()).isTrue();
        assertThat(summary.results()).hasSize(3);
        // Wall clock time should be close to max individual latency (~60ms) rather than sum (50+60+0 = 110ms)
        assertThat(summary.totalWallClockTimeMs()).isLessThan(300);
    }

    @Test
    @DisplayName("Should handle downstream 404 or connection failure gracefully")
    void shouldHandleDownstreamFailureGracefully() {
        String baseUrl = "http://localhost:" + port;
        List<DownstreamTarget> targets = List.of(
                DownstreamTarget.of("ValidService", baseUrl + "/recommendations", Duration.ofSeconds(2)),
                DownstreamTarget.of("NonExistentService", baseUrl + "/invalid-404-endpoint", Duration.ofSeconds(2))
        );

        GatewaySummary summary = gateway.executeScatterGather(targets);

        assertThat(summary.allSucceeded()).isFalse();
        assertThat(summary.results()).hasSize(2);
        assertThat(summary.results().stream().filter(r -> !r.success()).findFirst()).isPresent();
    }
}
