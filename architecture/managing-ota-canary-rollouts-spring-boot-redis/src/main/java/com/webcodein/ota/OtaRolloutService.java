package com.webcodein.ota;

import com.webcodein.ota.model.CanaryCheckRequest;
import com.webcodein.ota.model.CanaryCheckResponse;
import com.webcodein.ota.model.RolloutStatsResponse;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * High-performance OTA canary rollout service leveraging Redis Bitmaps.
 * Provides O(1) bitwise device eligibility lookups and sub-millisecond execution.
 */
@Service
public class OtaRolloutService {

    private static final String KEY_PREFIX = "rollout:";
    private static final String CDN_BASE_URL = "https://cdn.webcodein.com/firmware/";

    private final StringRedisTemplate redisTemplate;

    public OtaRolloutService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "StringRedisTemplate must not be null");
    }

    /**
     * Evaluates whether a device is authorized to receive a firmware update.
     *
     * @param deviceId Sequential integer device ID
     * @param version  Target firmware version
     * @return true if the bit is set to 1 in the rollout bitmap
     */
    public boolean isEligibleForUpdate(long deviceId, String version) {
        if (deviceId < 0 || version == null || version.isBlank()) {
            return false;
        }
        Boolean result = redisTemplate.opsForValue().getBit(KEY_PREFIX + version, deviceId);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Enables firmware update eligibility for a specific device by setting its bit to 1.
     */
    public void enableUpdateForDevice(long deviceId, String version) {
        validateInputs(deviceId, version);
        redisTemplate.opsForValue().setBit(KEY_PREFIX + version, deviceId, true);
    }

    /**
     * Revokes firmware update eligibility for a specific device by clearing its bit to 0.
     */
    public void disableUpdateForDevice(long deviceId, String version) {
        validateInputs(deviceId, version);
        redisTemplate.opsForValue().setBit(KEY_PREFIX + version, deviceId, false);
    }

    /**
     * Evaluates a full canary check request and returns structured response with CDN download URL.
     */
    public CanaryCheckResponse evaluateEligibility(CanaryCheckRequest request) {
        Objects.requireNonNull(request, "CanaryCheckRequest must not be null");
        boolean eligible = isEligibleForUpdate(request.deviceId(), request.targetVersion());

        if (eligible) {
            String downloadUrl = CDN_BASE_URL + request.deviceModel() + "/" + request.targetVersion() + "/update.bin";
            return CanaryCheckResponse.eligible(request.deviceId(), request.targetVersion(), downloadUrl, "ACTIVE_CANARY");
        }
        return CanaryCheckResponse.notEligible(request.deviceId(), request.targetVersion());
    }

    /**
     * Counts the exact number of devices currently eligible for the target version using Redis BITCOUNT.
     */
    public long countEnabledDevices(String version) {
        if (version == null || version.isBlank()) {
            return 0L;
        }
        byte[] keyBytes = (KEY_PREFIX + version).getBytes(StandardCharsets.UTF_8);
        Long count = redisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitCount(keyBytes));
        return count != null ? count : 0L;
    }

    /**
     * Returns statistical metrics for an active canary rollout.
     */
    public RolloutStatsResponse getRolloutStats(String version, long estimatedFleetSize) {
        long enabledCount = countEnabledDevices(version);
        return RolloutStatsResponse.of(version, enabledCount, estimatedFleetSize);
    }

    /**
     * Emergency kill-switch: purges the rollout key completely, instantly revoking access fleet-wide.
     */
    public boolean emergencyHaltRollout(String version) {
        if (version == null || version.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.delete(KEY_PREFIX + version));
    }

    private void validateInputs(long deviceId, String version) {
        if (deviceId < 0) {
            throw new IllegalArgumentException("Device ID must be non-negative: " + deviceId);
        }
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Version must not be null or blank");
        }
    }
}
