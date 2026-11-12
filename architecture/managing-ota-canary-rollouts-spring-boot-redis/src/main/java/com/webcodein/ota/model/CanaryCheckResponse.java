package com.webcodein.ota.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable response payload indicating device OTA eligibility.
 *
 * @param eligible      Whether the device is authorized to download target firmware
 * @param deviceId      Sequential integer device identifier
 * @param targetVersion Target firmware version evaluated
 * @param downloadUrl   CDN signed download URL when eligible, null otherwise
 * @param rolloutPhase  Current canary rollout phase (e.g., "1%", "5%", "100%", "BLOCKED")
 * @param timestamp     Instant when eligibility was determined
 */
public record CanaryCheckResponse(
        boolean eligible,
        long deviceId,
        String targetVersion,
        String downloadUrl,
        String rolloutPhase,
        Instant timestamp
) {
    public CanaryCheckResponse {
        Objects.requireNonNull(targetVersion, "Target version must not be null");
        Objects.requireNonNull(rolloutPhase, "Rollout phase must not be null");
        Objects.requireNonNull(timestamp, "Timestamp must not be null");
    }

    public static CanaryCheckResponse eligible(long deviceId, String version, String downloadUrl, String phase) {
        return new CanaryCheckResponse(true, deviceId, version, downloadUrl, phase, Instant.now());
    }

    public static CanaryCheckResponse notEligible(long deviceId, String version) {
        return new CanaryCheckResponse(false, deviceId, version, null, "INELIGIBLE", Instant.now());
    }
}
