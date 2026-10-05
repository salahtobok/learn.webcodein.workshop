package com.webcodein.architecture.flashsale.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FlashSaleService {
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> decrementInventoryScript;
    private final OrderProcessor orderProcessor;

    public FlashSaleService(StringRedisTemplate redisTemplate, RedisScript<Long> decrementInventoryScript, OrderProcessor orderProcessor) {
        this.redisTemplate = redisTemplate;
        this.decrementInventoryScript = decrementInventoryScript;
        this.orderProcessor = orderProcessor;
    }

    public boolean purchase(String productId, String userId) {
        String inventoryKey = "inventory:" + productId;
        
        Long result = redisTemplate.execute(
            decrementInventoryScript,
            List.of(inventoryKey),
            "1"
        );

        if (result != null && result == 1L) {
            orderProcessor.processOrder(productId, userId);
            return true;
        }
        
        return false;
    }
}
