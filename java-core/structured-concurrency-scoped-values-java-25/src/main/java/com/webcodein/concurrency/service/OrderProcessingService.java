package com.webcodein.concurrency.service;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.model.OrderRequest;
import com.webcodein.concurrency.model.OrderResult;
import com.webcodein.concurrency.model.RequestContext;
import com.webcodein.concurrency.model.ShippingQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final FraudDetectionService fraudDetectionService;
    private final ShippingCarrierService shippingCarrierService;

    public OrderProcessingService(
            InventoryService inventoryService,
            PaymentService paymentService,
            FraudDetectionService fraudDetectionService,
            ShippingCarrierService shippingCarrierService
    ) {
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        this.fraudDetectionService = fraudDetectionService;
        this.shippingCarrierService = shippingCarrierService;
    }

    /**
     * Executes order checkout using Structured Concurrency with fail-fast cancellation.
     * All subtasks run as lightweight Virtual Threads and automatically inherit the
     * active ScopedValue RequestContext from the parent caller.
     */
    public OrderResult processOrder(OrderRequest request) {
        long startTime = System.currentTimeMillis();
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Initiating structured order processing for {}",
                context.correlationId(), request.orderId());

        var joiner = StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow();
        try (var scope = StructuredTaskScope.open(joiner)) {
            // Fan-out: Fork concurrent subtasks
            Subtask<String> inventoryTask = scope.fork(() ->
                    inventoryService.reserveItems(request.orderId(), request.itemCount()));

            Subtask<String> paymentTask = scope.fork(() ->
                    paymentService.processPayment(request.orderId(), request.amount()));

            Subtask<Integer> fraudTask = scope.fork(() ->
                    fraudDetectionService.evaluateRisk(request.orderId(), request.customerId(), request.amount()));

            // Fan-in: Wait for all subtasks to complete or fail fast on the first exception
            scope.join();

            long duration = System.currentTimeMillis() - startTime;
            return new OrderResult(
                    request.orderId(),
                    "CONFIRMED",
                    inventoryTask.get(),
                    paymentTask.get(),
                    fraudTask.get(),
                    duration,
                    context.correlationId(),
                    Thread.currentThread().toString()
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Order processing scope interrupted", e);
        } catch (Throwable throwable) {
            Throwable cause = throwable instanceof java.util.concurrent.ExecutionException && throwable.getCause() != null
                    ? throwable.getCause()
                    : throwable;
            log.error("[Correlation: {}] Order processing failed during fan-out: {}",
                    context.correlationId(), cause.getMessage());
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException("Order processing failed", cause);
        }
    }

    /**
     * Executes speculative racing with StructuredTaskScope.Joiner.anySuccessfulOrThrow().
     * Returns the first valid shipping carrier quote and cancels the slower subtask.
     */
    public ShippingQuote getFastestShippingQuote(String origin, String destination) {
        RequestContext context = RequestContextScope.get();
        log.info("[Correlation: {}] Speculative racing for fastest shipping quote from {} to {}",
                context.correlationId(), origin, destination);

        var joiner = StructuredTaskScope.Joiner.<ShippingQuote>anySuccessfulOrThrow();
        try (var scope = StructuredTaskScope.open(joiner)) {
            scope.fork(() -> shippingCarrierService.fetchPrimaryCarrierQuote(origin, destination));
            scope.fork(() -> shippingCarrierService.fetchSecondaryCarrierQuote(origin, destination));

            scope.join();
            return joiner.result();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Shipping quote race interrupted", e);
        } catch (Throwable throwable) {
            Throwable cause = throwable instanceof java.util.concurrent.ExecutionException && throwable.getCause() != null
                    ? throwable.getCause()
                    : throwable;
            log.error("[Correlation: {}] Failed to retrieve shipping quotes: {}",
                    context.correlationId(), cause.getMessage());
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException("Quote query failed", cause);
        }
    }

    /**
     * Benchmark comparison demonstration between ScopedValue and ThreadLocal.
     */
    public Map<String, Object> benchmarkContextPassing(int iterations) {
        long startScoped = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            RequestContext tempContext = new RequestContext("bench-" + i, "user-" + i, "tenant-default", java.time.Instant.now());
            RequestContextScope.runWith(tempContext, () -> {
                RequestContext retrieved = RequestContextScope.get();
                if (!retrieved.correlationId().startsWith("bench-")) {
                    throw new IllegalStateException("Invalid context");
                }
            });
        }
        long scopedDurationNs = System.nanoTime() - startScoped;

        ThreadLocal<RequestContext> threadLocal = new ThreadLocal<>();
        long startThreadLocal = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            RequestContext tempContext = new RequestContext("bench-" + i, "user-" + i, "tenant-default", java.time.Instant.now());
            try {
                threadLocal.set(tempContext);
                RequestContext retrieved = threadLocal.get();
                if (!retrieved.correlationId().startsWith("bench-")) {
                    throw new IllegalStateException("Invalid context");
                }
            } finally {
                threadLocal.remove();
            }
        }
        long threadLocalDurationNs = System.nanoTime() - startThreadLocal;

        return Map.of(
                "iterations", iterations,
                "scopedValueDurationMs", scopedDurationNs / 1_000_000.0,
                "threadLocalDurationMs", threadLocalDurationNs / 1_000_000.0,
                "speedupPercentage", ((double) (threadLocalDurationNs - scopedDurationNs) / threadLocalDurationNs) * 100.0,
                "safetyGuarantee", "ScopedValue is immutable, strictly bound to call stack, and leak-free across virtual threads"
        );
    }
}
