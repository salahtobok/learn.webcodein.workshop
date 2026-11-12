package com.webcodein.ota.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RolloutStatsResponse Calculation Tests")
class RolloutStatsResponseTest {

    @Test
    @DisplayName("Should correctly calculate percentage ratio and round to two decimal places")
    void shouldCalculatePercentage() {
        RolloutStatsResponse stats = RolloutStatsResponse.of("v2.0", 500_000L, 10_000_000L);
        assertEquals("v2.0", stats.targetVersion());
        assertEquals(500_000L, stats.enabledDeviceCount());
        assertEquals(10_000_000L, stats.estimatedFleetSize());
        assertEquals(5.0, stats.effectivePercentage());
    }

    @Test
    @DisplayName("Should handle zero fleet size gracefully without division by zero error")
    void shouldHandleZeroFleetSize() {
        RolloutStatsResponse stats = RolloutStatsResponse.of("v2.0", 0L, 0L);
        assertEquals(0.0, stats.effectivePercentage());
    }

    @Test
    @DisplayName("Should reject negative counts")
    void shouldRejectNegativeCounts() {
        assertThrows(IllegalArgumentException.class, () ->
                new RolloutStatsResponse("v2.0", -10L, 100L, 0.0));
        assertThrows(IllegalArgumentException.class, () ->
                new RolloutStatsResponse("v2.0", 10L, -100L, 0.0));
    }
}
