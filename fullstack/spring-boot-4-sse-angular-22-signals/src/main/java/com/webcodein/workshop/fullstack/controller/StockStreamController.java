package com.webcodein.workshop.fullstack.controller;

import com.webcodein.workshop.fullstack.model.PriceUpdateEvent;
import com.webcodein.workshop.fullstack.model.StreamStats;
import com.webcodein.workshop.fullstack.service.StockTickerStreamService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/stream")
public class StockStreamController {

    private final StockTickerStreamService streamService;

    public StockStreamController(StockTickerStreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping(value = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribePrices(@RequestParam(required = false) String clientId) {
        String effectiveId = (clientId == null || clientId.isBlank())
                ? "client-" + UUID.randomUUID().toString().substring(0, 8)
                : clientId;
        return streamService.subscribe(effectiveId);
    }

    @PostMapping("/publish")
    public ResponseEntity<Map<String, Object>> publishUpdate(@Valid @RequestBody PriceUpdateEvent event) {
        int deliveredCount = streamService.broadcastPriceUpdate(event);
        return ResponseEntity.ok(Map.of(
                "ticker", event.ticker(),
                "deliveredToClients", deliveredCount,
                "sequenceNumber", event.sequenceNumber()
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<StreamStats> getStats() {
        return ResponseEntity.ok(streamService.getStats());
    }
}
