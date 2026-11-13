package com.webcodein.workshop.cache.controller;

import com.webcodein.workshop.cache.model.ProductDto;
import com.webcodein.workshop.cache.service.ProductCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductCatalogController {

    private final ProductCatalogService catalogService;

    public ProductCatalogController(ProductCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String id) {
        ProductDto product = catalogService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id, @Valid @RequestBody ProductDto product) {
        ProductDto updated = catalogService.updateProduct(id, product);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/cache/metrics")
    public ResponseEntity<Map<String, Object>> getCacheMetrics() {
        return ResponseEntity.ok(catalogService.getCacheStatistics());
    }

    @PostMapping("/cache/clear-l1")
    public ResponseEntity<Map<String, String>> clearL1() {
        catalogService.clearLocalL1Cache();
        return ResponseEntity.ok(Map.of("message", "Local L1 Caffeine cache cleared successfully"));
    }
}
