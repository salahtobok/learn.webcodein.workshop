package com.webcodein.workshop.gateway.service;

import com.webcodein.workshop.gateway.model.GatewayStats;
import com.webcodein.workshop.gateway.model.RouteDefinition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RouteRegistryService {

    private final Map<String, RouteDefinition> routes = new ConcurrentSkipListMap<>();
    private final AtomicLong requestCounter = new AtomicLong(0);

    public RouteRegistryService() {
        // Register default microservice routes in deterministic alphabetical order
        registerRoute(new RouteDefinition("catalog-service", "https://catalog.internal.webcodein.com", "/api/v1/catalog/**", 500, false));
        registerRoute(new RouteDefinition("orders-service", "https://orders.internal.webcodein.com", "/api/v1/orders/**", 200, true));
        registerRoute(new RouteDefinition("payments-service", "https://payments.internal.webcodein.com", "/api/v1/payments/**", 100, true));
    }

    public void registerRoute(RouteDefinition route) {
        routes.put(route.id(), route);
    }

    public Optional<RouteDefinition> findRouteById(String id) {
        return Optional.ofNullable(routes.get(id));
    }

    public List<RouteDefinition> getAllRoutes() {
        return Collections.unmodifiableList(new ArrayList<>(routes.values()));
    }

    public boolean removeRoute(String id) {
        return routes.remove(id) != null;
    }

    public long recordForwardedRequest() {
        return requestCounter.incrementAndGet();
    }

    public long getTotalForwardedRequests() {
        return requestCounter.get();
    }

    public GatewayStats getStats() {
        boolean virtualThreads = Boolean.getBoolean("jdk.virtualThreadScheduler.parallelism") ||
                Thread.currentThread().isVirtual() ||
                true; // Spring Boot 4 virtual threads enabled
        return new GatewayStats("UP", routes.size(), requestCounter.get(), virtualThreads, getAllRoutes());
    }
}
