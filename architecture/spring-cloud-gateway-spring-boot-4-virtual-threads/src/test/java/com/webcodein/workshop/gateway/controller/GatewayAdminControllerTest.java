package com.webcodein.workshop.gateway.controller;

import com.webcodein.workshop.gateway.model.RouteDefinition;
import com.webcodein.workshop.gateway.service.RouteRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GatewayAdminControllerTest {

    private MockMvc mockMvc;
    private RouteRegistryService routeService;

    @BeforeEach
    void setUp() {
        routeService = new RouteRegistryService();
        GatewayAdminController controller = new GatewayAdminController(routeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /admin/gateway/stats should return gateway stats")
    void shouldReturnGatewayStats() throws Exception {
        mockMvc.perform(get("/admin/gateway/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.activeRouteCount").value(3))
                .andExpect(jsonPath("$.virtualThreadsEnabled").value(true));
    }

    @Test
    @DisplayName("GET /admin/gateway/routes should return list of routes")
    void shouldReturnRoutesList() throws Exception {
        mockMvc.perform(get("/admin/gateway/routes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("catalog-service"))
                .andExpect(jsonPath("$[1].id").value("orders-service"))
                .andExpect(jsonPath("$[2].id").value("payments-service"));
    }

    @Test
    @DisplayName("GET /admin/gateway/routes/{id} should return single route")
    void shouldReturnSingleRoute() throws Exception {
        mockMvc.perform(get("/admin/gateway/routes/orders-service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("orders-service"))
                .andExpect(jsonPath("$.uri").value("https://orders.internal.webcodein.com"));
    }

    @Test
    @DisplayName("GET /admin/gateway/routes/{id} should return 404 when not found")
    void shouldReturn404ForUnknownRoute() throws Exception {
        mockMvc.perform(get("/admin/gateway/routes/unknown-route"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /admin/gateway/routes should register new route")
    void shouldRegisterNewRoute() throws Exception {
        String newRouteJson = """
                {
                    "id": "notification-service",
                    "uri": "https://notify.internal.webcodein.com",
                    "pathPattern": "/api/v1/notify/**",
                    "rateLimitRps": 300,
                    "requiresAuth": true
                }
                """;

        mockMvc.perform(post("/admin/gateway/routes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newRouteJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("notification-service"))
                .andExpect(jsonPath("$.rateLimitRps").value(300));
    }

    @Test
    @DisplayName("DELETE /admin/gateway/routes/{id} should remove route")
    void shouldDeleteRoute() throws Exception {
        mockMvc.perform(delete("/admin/gateway/routes/catalog-service"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/admin/gateway/routes/catalog-service"))
                .andExpect(status().isNotFound());
    }
}
