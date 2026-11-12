package com.webcodein.architecture.flashsale.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlashSaleServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private RedisScript<Long> decrementInventoryScript;

    @Mock
    private OrderProcessor orderProcessor;

    @InjectMocks
    private FlashSaleService flashSaleService;

    @Test
    @DisplayName("Should return true and trigger async order when inventory decrement succeeds")
    void shouldPurchaseSuccessfullyWhenStockAvailable() {
        when(redisTemplate.execute(eq(decrementInventoryScript), eq(List.of("inventory:PHONE_X")), eq("1")))
                .thenReturn(1L);

        boolean result = flashSaleService.purchase("PHONE_X", "USER_1");

        assertThat(result).isTrue();
        verify(orderProcessor).processOrder("PHONE_X", "USER_1");
    }

    @Test
    @DisplayName("Should return false and skip order creation when item is sold out")
    void shouldFailPurchaseWhenSoldOut() {
        when(redisTemplate.execute(eq(decrementInventoryScript), eq(List.of("inventory:PHONE_X")), eq("1")))
                .thenReturn(0L);

        boolean result = flashSaleService.purchase("PHONE_X", "USER_2");

        assertThat(result).isFalse();
        verify(orderProcessor, never()).processOrder(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return false when Redis returns null result")
    void shouldFailPurchaseWhenRedisReturnsNull() {
        when(redisTemplate.execute(eq(decrementInventoryScript), eq(List.of("inventory:PHONE_X")), eq("1")))
                .thenReturn(null);

        boolean result = flashSaleService.purchase("PHONE_X", "USER_3");

        assertThat(result).isFalse();
        verify(orderProcessor, never()).processOrder(anyString(), anyString());
    }
}
