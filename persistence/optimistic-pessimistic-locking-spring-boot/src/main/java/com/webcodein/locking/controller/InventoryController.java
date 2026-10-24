package com.webcodein.locking.controller;

import com.webcodein.locking.entity.Product;
import com.webcodein.locking.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/products/{id}/decrement-optimistic")
    public ResponseEntity<Product> decrementOptimistic(@PathVariable Long id,
                                                        @RequestParam(defaultValue = "1") int quantity) {
        Product product = inventoryService.decrementStockOptimistic(id, quantity);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products/{id}/decrement-optimistic-retry")
    public ResponseEntity<Product> decrementOptimisticWithRetry(@PathVariable Long id,
                                                                 @RequestParam(defaultValue = "1") int quantity,
                                                                 @RequestParam(defaultValue = "5") int retries) {
        Product product = inventoryService.decrementStockOptimisticWithRetry(id, quantity, retries);
        return ResponseEntity.ok(product);
    }

    @PostMapping("/products/{id}/decrement-pessimistic")
    public ResponseEntity<Product> decrementPessimistic(@PathVariable Long id,
                                                         @RequestParam(defaultValue = "1") int quantity) {
        Product product = inventoryService.decrementStockPessimistic(id, quantity);
        return ResponseEntity.ok(product);
    }
}
