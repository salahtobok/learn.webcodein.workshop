package com.webcodein.workshop.cache.unit;

import com.webcodein.workshop.cache.cache.TwoLevelCacheManager;
import com.webcodein.workshop.cache.model.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class TwoLevelCacheManagerTest {

    private TwoLevelCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new TwoLevelCacheManager();
    }

    @Test
    @DisplayName("Should query database on double miss and backfill both L1 and L2")
    void shouldLoadFromDatabaseAndBackfillCaches() {
        AtomicInteger dbQueries = new AtomicInteger(0);

        ProductDto p1 = cacheManager.get("P-100", ProductDto.class, id -> {
            dbQueries.incrementAndGet();
            return new ProductDto(id, "Test Item", new BigDecimal("50.00"), 10);
        });

        assertThat(p1).isNotNull();
        assertThat(p1.id()).isEqualTo("P-100");
        assertThat(dbQueries.get()).isEqualTo(1);

        Map<String, Object> metrics = cacheManager.getMetrics();
        assertThat(metrics.get("dbLoads")).isEqualTo(1L);
        assertThat(metrics.get("l1Hits")).isEqualTo(0L);
    }

    @Test
    @DisplayName("Second read should hit L1 Caffeine cache with zero database queries")
    void shouldHitL1CaffeineCache() {
        AtomicInteger dbQueries = new AtomicInteger(0);

        // First call populates L1 and L2
        cacheManager.get("P-100", ProductDto.class, id -> {
            dbQueries.incrementAndGet();
            return new ProductDto(id, "Test Item", new BigDecimal("50.00"), 10);
        });

        // Second call must hit L1 directly
        ProductDto cached = cacheManager.get("P-100", ProductDto.class, id -> {
            dbQueries.incrementAndGet();
            return null;
        });

        assertThat(cached).isNotNull();
        assertThat(dbQueries.get()).isEqualTo(1);

        Map<String, Object> metrics = cacheManager.getMetrics();
        assertThat(metrics.get("l1Hits")).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should hit L2 Redis cache when L1 is invalidated and backfill L1")
    void shouldHitL2WhenL1IsInvalidated() {
        AtomicInteger dbQueries = new AtomicInteger(0);

        // Initial load
        cacheManager.get("P-100", ProductDto.class, id -> {
            dbQueries.incrementAndGet();
            return new ProductDto(id, "Test Item", new BigDecimal("50.00"), 10);
        });

        // Clear local L1 only
        cacheManager.clearL1();

        // Next read misses L1, hits L2, and backfills L1 without calling DB
        ProductDto l2Hit = cacheManager.get("P-100", ProductDto.class, id -> {
            dbQueries.incrementAndGet();
            return null;
        });

        assertThat(l2Hit).isNotNull();
        assertThat(dbQueries.get()).isEqualTo(1);

        Map<String, Object> metrics = cacheManager.getMetrics();
        assertThat(metrics.get("l2Hits")).isEqualTo(1L);

        // Next read hits L1 again
        cacheManager.get("P-100", ProductDto.class, id -> null);
        assertThat(((Long) cacheManager.getMetrics().get("l1Hits"))).isEqualTo(1L);
    }

    @Test
    @DisplayName("Eviction should remove key from both L1 and L2")
    void shouldEvictFromBothTiers() {
        cacheManager.put("P-200", new ProductDto("P-200", "Evict Me", new BigDecimal("20.00"), 1));
        cacheManager.evict("P-200");

        AtomicInteger dbQueries = new AtomicInteger(0);
        cacheManager.get("P-200", ProductDto.class, id -> {
            dbQueries.incrementAndGet();
            return new ProductDto(id, "Reloaded", new BigDecimal("25.00"), 2);
        });

        assertThat(dbQueries.get()).isEqualTo(1);
    }
}
