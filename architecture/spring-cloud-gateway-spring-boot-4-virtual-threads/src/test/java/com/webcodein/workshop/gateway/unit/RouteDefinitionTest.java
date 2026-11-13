package com.webcodein.workshop.gateway.unit;

import com.webcodein.workshop.gateway.model.RouteDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RouteDefinitionTest {

    @Test
    @DisplayName("Should create valid RouteDefinition")
    void shouldCreateValidRouteDefinition() {
        RouteDefinition route = new RouteDefinition(
                "orders-api",
                "https://orders.service.internal",
                "/api/orders/**",
                150,
                true
        );

        assertThat(route.id()).isEqualTo("orders-api");
        assertThat(route.uri()).isEqualTo("https://orders.service.internal");
        assertThat(route.pathPattern()).isEqualTo("/api/orders/**");
        assertThat(route.rateLimitRps()).isEqualTo(150);
        assertThat(route.requiresAuth()).isTrue();
    }

    @Test
    @DisplayName("Should fail when URI is invalid")
    void shouldFailWhenUriIsInvalid() {
        assertThatThrownBy(() -> new RouteDefinition("id", "invalid-uri", "/path", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Route URI must start with http:// or https://");
    }

    @Test
    @DisplayName("Should fail when pathPattern does not start with slash")
    void shouldFailWhenPathPatternIsInvalid() {
        assertThatThrownBy(() -> new RouteDefinition("id", "https://valid.com", "path", 100, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Path pattern must start with /");
    }

    @Test
    @DisplayName("Should fail when rate limit is zero or negative")
    void shouldFailWhenRateLimitIsNonPositive() {
        assertThatThrownBy(() -> new RouteDefinition("id", "https://valid.com", "/path", 0, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rate limit RPS must be positive");
    }
}
