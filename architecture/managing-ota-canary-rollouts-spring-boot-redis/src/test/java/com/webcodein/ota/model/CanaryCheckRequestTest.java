package com.webcodein.ota.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CanaryCheckRequest Record Invariant Tests")
class CanaryCheckRequestTest {

    @Test
    @DisplayName("Should create valid request record when all parameters are valid")
    void shouldCreateValidRequest() {
        CanaryCheckRequest request = new CanaryCheckRequest(10542L, "v2.1.0", "Pixel-9-Pro", "v2.0.4");
        assertEquals(10542L, request.deviceId());
        assertEquals("v2.1.0", request.targetVersion());
        assertEquals("Pixel-9-Pro", request.deviceModel());
        assertEquals("v2.0.4", request.currentVersion());
    }

    @Test
    @DisplayName("Should throw exception when deviceId is negative")
    void shouldThrowWhenDeviceIdNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new CanaryCheckRequest(-1L, "v2.1.0", "Pixel-9-Pro", "v2.0.4"));
    }

    @Test
    @DisplayName("Should throw exception when targetVersion is blank or null")
    void shouldThrowWhenTargetVersionInvalid() {
        assertThrows(NullPointerException.class, () ->
                new CanaryCheckRequest(100L, null, "Pixel-9-Pro", "v2.0.4"));
        assertThrows(IllegalArgumentException.class, () ->
                new CanaryCheckRequest(100L, "   ", "Pixel-9-Pro", "v2.0.4"));
    }

    @Test
    @DisplayName("Should throw exception when deviceModel is blank or null")
    void shouldThrowWhenDeviceModelInvalid() {
        assertThrows(NullPointerException.class, () ->
                new CanaryCheckRequest(100L, "v2.1.0", null, "v2.0.4"));
        assertThrows(IllegalArgumentException.class, () ->
                new CanaryCheckRequest(100L, "v2.1.0", "", "v2.0.4"));
    }

    @Test
    @DisplayName("Should throw exception when currentVersion is blank or null")
    void shouldThrowWhenCurrentVersionInvalid() {
        assertThrows(NullPointerException.class, () ->
                new CanaryCheckRequest(100L, "v2.1.0", "Pixel-9-Pro", null));
        assertThrows(IllegalArgumentException.class, () ->
                new CanaryCheckRequest(100L, "v2.1.0", "Pixel-9-Pro", " "));
    }
}
