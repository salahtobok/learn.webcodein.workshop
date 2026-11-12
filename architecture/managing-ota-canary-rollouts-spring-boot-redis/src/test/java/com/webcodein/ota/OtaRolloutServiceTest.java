package com.webcodein.ota;

import com.webcodein.ota.model.CanaryCheckRequest;
import com.webcodein.ota.model.CanaryCheckResponse;
import com.webcodein.ota.model.RolloutStatsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OtaRolloutService Unit Tests")
class OtaRolloutServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private OtaRolloutService service;

    @BeforeEach
    void setUp() {
        service = new OtaRolloutService(redisTemplate);
    }

    @Test
    @DisplayName("Should return true when device bit is 1 in Redis")
    void shouldReturnTrueWhenDeviceBitSet() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getBit("rollout:v2.0", 100L)).thenReturn(true);

        assertTrue(service.isEligibleForUpdate(100L, "v2.0"));
        verify(valueOperations).getBit("rollout:v2.0", 100L);
    }

    @Test
    @DisplayName("Should return false when device bit is 0 or null")
    void shouldReturnFalseWhenDeviceBitUnset() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getBit("rollout:v2.0", 200L)).thenReturn(false);

        assertFalse(service.isEligibleForUpdate(200L, "v2.0"));

        when(valueOperations.getBit("rollout:v2.0", 201L)).thenReturn(null);
        assertFalse(service.isEligibleForUpdate(201L, "v2.0"));
    }

    @Test
    @DisplayName("Should return false for invalid negative deviceId or blank version")
    void shouldReturnFalseForInvalidInputs() {
        assertFalse(service.isEligibleForUpdate(-5L, "v2.0"));
        assertFalse(service.isEligibleForUpdate(100L, ""));
        assertFalse(service.isEligibleForUpdate(100L, null));
    }

    @Test
    @DisplayName("Should set bit to true when enabling device")
    void shouldEnableDevice() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.enableUpdateForDevice(500L, "v2.0");
        verify(valueOperations).setBit("rollout:v2.0", 500L, true);
    }

    @Test
    @DisplayName("Should set bit to false when disabling device")
    void shouldDisableDevice() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service.disableUpdateForDevice(500L, "v2.0");
        verify(valueOperations).setBit("rollout:v2.0", 500L, false);
    }

    @Test
    @DisplayName("Should evaluate eligibility and build full CDN URL when eligible")
    void shouldEvaluateEligibleDevice() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getBit("rollout:v2.5.0", 777L)).thenReturn(true);

        CanaryCheckRequest request = new CanaryCheckRequest(777L, "v2.5.0", "Pixel-9", "v2.4.0");
        CanaryCheckResponse response = service.evaluateEligibility(request);

        assertTrue(response.eligible());
        assertEquals("https://cdn.webcodein.com/firmware/Pixel-9/v2.5.0/update.bin", response.downloadUrl());
        assertEquals("ACTIVE_CANARY", response.rolloutPhase());
    }

    @Test
    @DisplayName("Should evaluate ineligibility when bit is unset")
    void shouldEvaluateIneligibleDevice() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.getBit("rollout:v2.5.0", 888L)).thenReturn(false);

        CanaryCheckRequest request = new CanaryCheckRequest(888L, "v2.5.0", "Pixel-9", "v2.4.0");
        CanaryCheckResponse response = service.evaluateEligibility(request);

        assertFalse(response.eligible());
        assertNull(response.downloadUrl());
        assertEquals("INELIGIBLE", response.rolloutPhase());
    }

    @Test
    @DisplayName("Should delete key upon emergency halt")
    void shouldDeleteKeyUponEmergencyHalt() {
        when(redisTemplate.delete("rollout:v2.0")).thenReturn(true);

        assertTrue(service.emergencyHaltRollout("v2.0"));
        verify(redisTemplate).delete("rollout:v2.0");
    }
}
