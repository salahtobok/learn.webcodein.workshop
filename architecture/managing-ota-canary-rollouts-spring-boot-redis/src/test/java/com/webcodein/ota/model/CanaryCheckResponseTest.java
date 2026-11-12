package com.webcodein.ota.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CanaryCheckResponse Factory & Invariant Tests")
class CanaryCheckResponseTest {

    @Test
    @DisplayName("Should create eligible response with CDN download URL")
    void shouldCreateEligibleResponse() {
        CanaryCheckResponse response = CanaryCheckResponse.eligible(
                45000L,
                "v3.0.0",
                "https://cdn.webcodein.com/firmware/Pixel-9/v3.0.0/update.bin",
                "CANARY_5_PERCENT"
        );

        assertTrue(response.eligible());
        assertEquals(45000L, response.deviceId());
        assertEquals("v3.0.0", response.targetVersion());
        assertEquals("https://cdn.webcodein.com/firmware/Pixel-9/v3.0.0/update.bin", response.downloadUrl());
        assertEquals("CANARY_5_PERCENT", response.rolloutPhase());
        assertNotNull(response.timestamp());
    }

    @Test
    @DisplayName("Should create not eligible response with null download URL")
    void shouldCreateNotEligibleResponse() {
        CanaryCheckResponse response = CanaryCheckResponse.notEligible(45000L, "v3.0.0");

        assertFalse(response.eligible());
        assertEquals(45000L, response.deviceId());
        assertEquals("v3.0.0", response.targetVersion());
        assertNull(response.downloadUrl());
        assertEquals("INELIGIBLE", response.rolloutPhase());
        assertNotNull(response.timestamp());
    }
}
