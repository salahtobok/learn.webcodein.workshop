package com.webcodein.workshop.protocol;

import com.webcodein.workshop.protocol.model.BenchmarkOrder;
import com.webcodein.workshop.protocol.model.BenchmarkResult;
import com.webcodein.workshop.protocol.model.ProtocolType;
import com.webcodein.workshop.protocol.service.BenchmarkExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BenchmarkExecutionServiceTest {

    private BenchmarkExecutionService service;

    @BeforeEach
    void setUp() {
        service = new BenchmarkExecutionService();
    }

    @Test
    @DisplayName("gRPC Protobuf achieves smaller payload size than REST JSON")
    void testPayloadComparison() {
        BenchmarkOrder order = BenchmarkOrder.sample();
        BenchmarkResult rest = service.benchmarkRestJson(order);
        BenchmarkResult grpc = service.benchmarkGrpcProtobuf(order);

        assertThat(rest.protocol()).isEqualTo(ProtocolType.REST_JSON);
        assertThat(grpc.protocol()).isEqualTo(ProtocolType.GRPC_PROTOBUF);
        assertThat(grpc.payloadSizeBytes()).isLessThan(rest.payloadSizeBytes());
        assertThat(grpc.totalRoundTripNanos()).isLessThan(rest.totalRoundTripNanos());
    }
}
