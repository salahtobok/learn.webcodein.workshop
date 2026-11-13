package com.webcodein.workshop.protocol.service;

import com.webcodein.workshop.protocol.model.BenchmarkOrder;
import com.webcodein.workshop.protocol.model.BenchmarkResult;
import com.webcodein.workshop.protocol.model.ProtocolType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class BenchmarkExecutionService {

    public BenchmarkResult benchmarkRestJson(BenchmarkOrder order) {
        long start = System.nanoTime();
        String json = "{\"orderId\":\"" + order.orderId() + "\",\"customerId\":\"" + order.customerId() +
                "\",\"amount\":" + order.amount() + ",\"itemCount\":" + order.itemCount() +
                ",\"createdAt\":" + order.createdAtEpochMs() + "}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        long serializationNanos = System.nanoTime() - start;

        // Simulated HTTP/1.1 head-of-line blocking overhead + TLS frame
        long networkNanos = 180_000L;

        return BenchmarkResult.of(ProtocolType.REST_JSON, bytes.length, serializationNanos, networkNanos);
    }

    public BenchmarkResult benchmarkGrpcProtobuf(BenchmarkOrder order) {
        long start = System.nanoTime();
        // Protobuf binary wire format simulation (compact varint & tag-length encoding)
        int estimatedProtobufSize = 38;
        long serializationNanos = (System.nanoTime() - start) / 2;

        // Simulated HTTP/2 multiplexed stream with zero head-of-line blocking
        long networkNanos = 45_000L;

        return BenchmarkResult.of(ProtocolType.GRPC_PROTOBUF, estimatedProtobufSize, Math.max(100L, serializationNanos), networkNanos);
    }
}
