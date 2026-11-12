package com.webcodein.ota.model;

import java.util.Objects;

/**
 * Immutable metrics report summarizing canary rollout adoption in Redis.
 *
 * @param targetVersion       Firmware version
 * @param enabledDeviceCount  Exact number of devices with bit set to 1 (via BITCOUNT)
 * @param estimatedFleetSize  Total fleet size evaluated against
 * @param effectivePercentage Calculated adoption ratio (0.0 to 100.0)
 */
public record RolloutStatsResponse(
        String targetVersion,
        long enabledDeviceCount,
        long estimatedFleetSize,
        double effectivePercentage
) {
    public RolloutStatsResponse {
        Objects.requireNonNull(targetVersion, "Target version must not be null");
        if (enabledDeviceCount < 0) {
            throw new IllegalArgumentException("Enabled device count must be non-negative");
        }
        if (estimatedFleetSize < 0) {
            throw new IllegalArgumentException("Estimated fleet size must be non-negative");
        }
    }

    public static RolloutStatsResponse of(String targetVersion, long enabledCount, long fleetSize) {
        double percentage = fleetSize > 0 ? ((double) enabledCount / fleetSize) * 100.0 : 0.0;
        return new RolloutStatsResponse(targetVersion, enabledCount, fleetSize, Math.round(percentage * 100.0) / 100.0);
    }
}
