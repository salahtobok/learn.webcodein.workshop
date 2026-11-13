package com.webcodein.workshop.cache.service;

import com.webcodein.workshop.cache.cache.TwoLevelCacheManager;
import com.webcodein.workshop.cache.model.ProductDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductCatalogService {

    private final TwoLevelCacheManager cacheManager;

    // Simulated Primary Database (System of Record)
    private final Map<String, ProductDto> database = new ConcurrentHashMap<>();

    public ProductCatalogService(TwoLevelCacheManager cacheManager) {
        this.cacheManager = cacheManager;
        // Seed database
        database.put("PROD-101", new ProductDto("PROD-101", "Ultra High-Speed Cloud Router", new BigDecimal("499.99"), 50));
        database.put("PROD-102", new ProductDto("PROD-102", "Smart Enterprise Sensor Hub", new BigDecimal("129.50"), 120));
        database.put("PROD-103", new ProductDto("PROD-103", "Precision Fiber Transceiver", new BigDecimal("89.00"), 300));
    }

    public ProductDto getProductById(String id) {
        return cacheManager.get(id, ProductDto.class, database::get);
    }

    public ProductDto updateProduct(String id, ProductDto updated) {
        database.put(id, updated);
        // Evict from both L1 and broadcast L2 invalidation
        cacheManager.evict(id);
        return updated;
    }

    public Map<String, Object> getCacheStatistics() {
        return cacheManager.getMetrics();
    }

    public void clearLocalL1Cache() {
        cacheManager.clearL1();
    }
}
