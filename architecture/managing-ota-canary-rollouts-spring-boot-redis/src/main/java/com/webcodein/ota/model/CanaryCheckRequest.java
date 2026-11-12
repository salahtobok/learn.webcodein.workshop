package com.webcodein.ota.model;

import java.util.Objects;

/**
 * Immutable request payload for OTA canary eligibility check.
 *
 * @param deviceId       Sequential integer device identifier (0 to N)
 * @param targetVersion  Target firmware version to evaluate (e.g., "v2.0.0")
 * @param deviceModel    Hardware hardware revision or SKU
 * @param currentVersion Currently installed firmware version
 */
public record CanaryCheckRequest(
        long deviceId,
        String targetVersion,
        String deviceModel,
        String currentVersion
) {
    public CanaryCheckRequest {
        if (deviceId < 0) {
            throw new IllegalArgumentException("Device ID must be non-negative: " + deviceId);
        }
        Objects.requireNonNull(targetVersion, "Target version must not be null");
        if (targetVersion.isBlank()) {
            throw new IllegalArgumentException("Target version must not be blank");
        }
        Objects.requireNonNull(deviceModel, "Device model must not be null");
        if (deviceModel.isBlank()) {
            throw new IllegalArgumentException("Device model must not be blank");
        }
        Objects.requireNonNull(currentVersion, "Current version must not be null");
        if (currentVersion.isBlank()) {
            throw new IllegalArgumentException("Current version must not be blank");
        }
    }
}
