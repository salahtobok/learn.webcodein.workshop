package com.webcodein.concurrency.web;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.model.OrderRequest;
import com.webcodein.concurrency.model.OrderResult;
import com.webcodein.concurrency.model.RequestContext;
import com.webcodein.concurrency.model.ShippingQuote;
import com.webcodein.concurrency.service.OrderProcessingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderCheckoutController {

    private final OrderProcessingService orderProcessingService;

    public OrderCheckoutController(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @PostMapping("/orders/checkout")
    public ResponseEntity<OrderResult> checkout(@Valid @RequestBody OrderRequest request) {
        OrderResult result = orderProcessingService.processOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/shipping/quotes/fastest")
    public ResponseEntity<ShippingQuote> getFastestShippingQuote(
            @RequestParam(defaultValue = "JFK") String origin,
            @RequestParam(defaultValue = "LHR") String destination
    ) {
        ShippingQuote quote = orderProcessingService.getFastestShippingQuote(origin, destination);
        return ResponseEntity.ok(quote);
    }

    @GetMapping("/benchmarks/scoping")
    public ResponseEntity<Map<String, Object>> benchmarkScoping(
            @RequestParam(defaultValue = "100000") int iterations
    ) {
        Map<String, Object> results = orderProcessingService.benchmarkContextPassing(iterations);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/context/current")
    public ResponseEntity<RequestContext> getCurrentContext() {
        return ResponseEntity.ok(RequestContextScope.get());
    }
}
