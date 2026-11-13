package com.webcodein.workshop.protocol.controller;

import com.webcodein.workshop.protocol.model.BenchmarkOrder;
import com.webcodein.workshop.protocol.model.BenchmarkResult;
import com.webcodein.workshop.protocol.service.BenchmarkExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/benchmark")
public class ProtocolBenchmarkController {

    private final BenchmarkExecutionService benchmarkService;

    public ProtocolBenchmarkController(BenchmarkExecutionService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    @PostMapping("/rest")
    public ResponseEntity<BenchmarkResult> runRestBenchmark(@RequestBody(required = false) BenchmarkOrder order) {
        BenchmarkOrder target = (order != null) ? order : BenchmarkOrder.sample();
        return ResponseEntity.ok(benchmarkService.benchmarkRestJson(target));
    }

    @PostMapping("/grpc")
    public ResponseEntity<BenchmarkResult> runGrpcBenchmark(@RequestBody(required = false) BenchmarkOrder order) {
        BenchmarkOrder target = (order != null) ? order : BenchmarkOrder.sample();
        return ResponseEntity.ok(benchmarkService.benchmarkGrpcProtobuf(target));
    }

    @GetMapping("/comparison")
    public ResponseEntity<Map<String, Object>> getComparison() {
        BenchmarkOrder sample = BenchmarkOrder.sample();
        BenchmarkResult rest = benchmarkService.benchmarkRestJson(sample);
        BenchmarkResult grpc = benchmarkService.benchmarkGrpcProtobuf(sample);

        double payloadReduction = 100.0 * (1.0 - ((double) grpc.payloadSizeBytes() / rest.payloadSizeBytes()));

        return ResponseEntity.ok(Map.of(
                "restResult", rest,
                "grpcResult", grpc,
                "payloadReductionPercent", Math.round(payloadReduction * 100.0) / 100.0,
                "recommendation", "Use gRPC for high-throughput internal microservices and REST for public external gateways"
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "benchmarks", "REST JSON vs gRPC Protobuf"
        ));
    }
}
