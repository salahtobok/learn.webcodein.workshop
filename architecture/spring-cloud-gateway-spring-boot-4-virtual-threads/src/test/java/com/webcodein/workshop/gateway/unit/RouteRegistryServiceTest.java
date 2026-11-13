package com.webcodein.workshop.gateway.unit;

import com.webcodein.workshop.gateway.model.GatewayStats;
import com.webcodein.workshop.gateway.model.RouteDefinition;
import com.webcodein.workshop.gateway.service.RouteRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RouteRegistryServiceTest {

    private RouteRegistryService service;

    @BeforeEach
    void setUp() {
        service = new RouteRegistryService();
    }

    @Test
    @DisplayName("Should initialize with default microservice routes")
    void shouldInitializeWithDefaultRoutes() {
        assertThat(service.getAllRoutes()).hasSizeGreaterThanOrEqualTo(3);
        assertThat(service.findRouteById("orders-service")).isPresent();
        assertThat(service.findRouteById("payments-service")).isPresent();
        assertThat(service.findRouteById("catalog-service")).isPresent();
    }

    @Test
    @DisplayName("Should register and retrieve a new route")
    void shouldRegisterAndRetrieveRoute() {
        RouteDefinition customRoute = new RouteDefinition(
                "auth-service",
                "https://auth.internal.webcodein.com",
                "/api/auth/**",
                50,
                false
        );

        service.registerRoute(customRoute);
        Optional<RouteDefinition> retrieved = service.findRouteById("auth-service");

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().uri()).isEqualTo("https://auth.internal.webcodein.com");
    }

    @Test
    @DisplayName("Should remove route correctly")
    void shouldRemoveRoute() {
        boolean removed = service.removeRoute("catalog-service");
        assertThat(removed).isTrue();
        assertThat(service.findRouteById("catalog-service")).isEmpty();
    }

    @Test
    @DisplayName("Should record forwarded requests and provide stats")
    void shouldRecordForwardedRequestsAndProvideStats() {
        long c1 = service.recordForwardedRequest();
        long c2 = service.recordForwardedRequest();

        assertThat(c2).isEqualTo(c1 + 1);
        GatewayStats stats = service.getStats();

        assertThat(stats.status()).isEqualTo("UP");
        assertThat(stats.totalForwardedRequests()).isEqualTo(c2);
        assertThat(stats.activeRouteCount()).isEqualTo(service.getAllRoutes().size());
    }
}
