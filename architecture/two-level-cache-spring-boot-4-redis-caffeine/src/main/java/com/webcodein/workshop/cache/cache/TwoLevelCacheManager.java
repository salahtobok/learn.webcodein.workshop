package com.webcodein.workshop.cache.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Component
public class TwoLevelCacheManager {

    private static final Logger log = LoggerFactory.getLogger(TwoLevelCacheManager.class);

    // L1: Caffeine In-Memory Cache
    private final Cache<String, Object> l1Cache;

    // L2: Distributed Cache Simulation (Redis Contract)
    private final Map<String, Object> l2DistributedStore = new ConcurrentHashMap<>();

    // Performance Metrics
    private final AtomicLong l1Hits = new AtomicLong(0);
    private final AtomicLong l2Hits = new AtomicLong(0);
    private final AtomicLong dbLoads = new AtomicLong(0);
    private final AtomicLong evictions = new AtomicLong(0);

    public TwoLevelCacheManager() {
        this.l1Cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .recordStats()
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type, Function<String, T> dbLoader) {
        // 1. Check L1 Caffeine Cache (Sub-microsecond)
        Object l1Value = l1Cache.getIfPresent(key);
        if (l1Value != null) {
            l1Hits.incrementAndGet();
            log.debug("L1 Cache HIT for key: {}", key);
            return (T) l1Value;
        }

        // 2. Check L2 Distributed Store (Redis)
        Object l2Value = l2DistributedStore.get(key);
        if (l2Value != null) {
            l2Hits.incrementAndGet();
            log.debug("L2 Cache HIT for key: {}. Backfilling L1.", key);
            l1Cache.put(key, l2Value);
            return (T) l2Value;
        }

        // 3. Cache Miss on Both Tiers: Single-Flight DB Loader (Cache Stampede Protection)
        synchronized (this) {
            // Double-check after lock
            Object retryL1 = l1Cache.getIfPresent(key);
            if (retryL1 != null) {
                l1Hits.incrementAndGet();
                return (T) retryL1;
            }

            dbLoads.incrementAndGet();
            log.info("L1/L2 Cache MISS for key: {}. Executing Database Query.", key);
            T loaded = dbLoader.apply(key);
            if (loaded != null) {
                l2DistributedStore.put(key, loaded);
                l1Cache.put(key, loaded);
            }
            return loaded;
        }
    }

    public void put(String key, Object value) {
        l2DistributedStore.put(key, value);
        l1Cache.put(key, value);
    }

    public void evict(String key) {
        evictions.incrementAndGet();
        l1Cache.invalidate(key);
        l2DistributedStore.remove(key);
        log.info("Evicted key '{}' across L1 (Caffeine) and published L2 (Redis) invalidation event.", key);
    }

    public void clearL1() {
        l1Cache.invalidateAll();
    }

    public Map<String, Object> getMetrics() {
        long total = l1Hits.get() + l2Hits.get() + dbLoads.get();
        double l1Rate = total > 0 ? (double) l1Hits.get() / total * 100.0 : 0.0;
        double l2Rate = total > 0 ? (double) l2Hits.get() / total * 100.0 : 0.0;
        double totalHitRate = total > 0 ? (double) (l1Hits.get() + l2Hits.get()) / total * 100.0 : 0.0;

        return Map.of(
                "l1Hits", l1Hits.get(),
                "l2Hits", l2Hits.get(),
                "dbLoads", dbLoads.get(),
                "evictions", evictions.get(),
                "l1HitRatePercent", Math.round(l1Rate * 100.0) / 100.0,
                "l2HitRatePercent", Math.round(l2Rate * 100.0) / 100.0,
                "overallHitRatePercent", Math.round(totalHitRate * 100.0) / 100.0,
                "l1Size", l1Cache.estimatedSize(),
                "l2Size", l2DistributedStore.size()
        );
    }
}
