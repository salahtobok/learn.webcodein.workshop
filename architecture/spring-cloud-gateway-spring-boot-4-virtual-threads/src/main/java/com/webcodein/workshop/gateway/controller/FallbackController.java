package com.webcodein.workshop.gateway.controller;

import com.webcodein.workshop.gateway.model.FallbackResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{routeId}")
    public ResponseEntity<FallbackResponse> getFallback(@PathVariable String routeId) {
        FallbackResponse fallback = new FallbackResponse(
                routeId,
                "Downstream service is temporarily unavailable. Returning cached fallback response.",
                "SERVICE_UNAVAILABLE_FALLBACK",
                true,
                Instant.now()
        );
        return ResponseEntity.ok()
                .header("X-Gateway-Degraded", "true")
                .body(fallback);
    }
}
