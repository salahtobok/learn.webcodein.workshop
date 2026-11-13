package com.webcodein.workshop.gateway.controller;

import com.webcodein.workshop.gateway.model.GatewayStats;
import com.webcodein.workshop.gateway.model.RouteDefinition;
import com.webcodein.workshop.gateway.service.RouteRegistryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/gateway")
public class GatewayAdminController {

    private final RouteRegistryService routeService;

    public GatewayAdminController(RouteRegistryService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/stats")
    public ResponseEntity<GatewayStats> getStats() {
        return ResponseEntity.ok(routeService.getStats());
    }

    @GetMapping("/routes")
    public ResponseEntity<List<RouteDefinition>> getRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteDefinition> getRoute(@PathVariable String id) {
        return routeService.findRouteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/routes")
    public ResponseEntity<RouteDefinition> createRoute(@Valid @RequestBody RouteDefinition route) {
        routeService.registerRoute(route);
        return ResponseEntity.status(HttpStatus.CREATED).body(route);
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable String id) {
        if (routeService.removeRoute(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
