package com.webcodein.workshop.kafka.controller;

import com.webcodein.workshop.kafka.model.DltMessageRecord;
import com.webcodein.workshop.kafka.model.OrderEvent;
import com.webcodein.workshop.kafka.model.RetryStats;
import com.webcodein.workshop.kafka.service.KafkaRetryConsumerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dlt")
public class DltAdminController {

    private final KafkaRetryConsumerService consumerService;

    public DltAdminController(KafkaRetryConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @GetMapping("/messages")
    public ResponseEntity<List<DltMessageRecord>> getMessages() {
        return ResponseEntity.ok(consumerService.getAllDltMessages());
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<DltMessageRecord> getMessage(@PathVariable String id) {
        return consumerService.findDltMessage(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/replay/{id}")
    public ResponseEntity<Map<String, Object>> replayMessage(@PathVariable String id) {
        boolean success = consumerService.replayMessage(id);
        if (success) {
            return ResponseEntity.ok(Map.of("messageId", id, "status", "REPLAYED_TO_MAIN_TOPIC"));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Message not found or already replayed", "messageId", id));
    }

    @GetMapping("/stats")
    public ResponseEntity<RetryStats> getStats() {
        return ResponseEntity.ok(consumerService.getStats());
    }

    @PostMapping("/simulate")
    public ResponseEntity<Map<String, String>> simulateEvent(@Valid @RequestBody OrderEvent event) {
        String result = consumerService.processOrderEvent(event);
        return ResponseEntity.ok(Map.of("orderId", event.orderId(), "action", result));
    }
}
