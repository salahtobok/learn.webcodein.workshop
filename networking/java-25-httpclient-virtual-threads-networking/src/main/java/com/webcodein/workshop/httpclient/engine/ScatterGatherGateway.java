package com.webcodein.workshop.httpclient.engine;

import com.webcodein.workshop.httpclient.model.AggregatedResponse;
import com.webcodein.workshop.httpclient.model.DownstreamTarget;
import com.webcodein.workshop.httpclient.model.GatewaySummary;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ScatterGatherGateway {

    private final HttpClient httpClient;

    public ScatterGatherGateway() {
        this(ResilientHttpClientFactory.createVirtualThreadHttpClient());
    }

    public ScatterGatherGateway(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
    }

    public GatewaySummary executeScatterGather(List<DownstreamTarget> targets) {
        Objects.requireNonNull(targets, "targets must not be null");

        long overallStart = System.currentTimeMillis();
        List<AggregatedResponse> results = new ArrayList<>();
        boolean allSucceeded = true;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<AggregatedResponse>> futures = new ArrayList<>();

            for (DownstreamTarget target : targets) {
                futures.add(executor.submit(() -> fetchTarget(target)));
            }

            for (Future<AggregatedResponse> future : futures) {
                try {
                    AggregatedResponse response = future.get();
                    results.add(response);
                    if (!response.success()) {
                        allSucceeded = false;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    allSucceeded = false;
                    results.add(AggregatedResponse.failed("UNKNOWN", 500, e.getMessage(), 0));
                }
            }
        }

        long totalDurationMs = System.currentTimeMillis() - overallStart;
        return new GatewaySummary(results, totalDurationMs, allSucceeded);
    }

    private AggregatedResponse fetchTarget(DownstreamTarget target) {
        long start = System.currentTimeMillis();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(target.targetUri())
                .timeout(target.timeout())
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long latencyMs = System.currentTimeMillis() - start;

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return AggregatedResponse.ok(target.serviceName(), response.statusCode(), response.body(), latencyMs);
            } else {
                return AggregatedResponse.failed(target.serviceName(), response.statusCode(), response.body(), latencyMs);
            }
        } catch (IOException | InterruptedException e) {
            long latencyMs = System.currentTimeMillis() - start;
            return AggregatedResponse.failed(target.serviceName(), 504, e.getMessage(), latencyMs);
        }
    }
}
