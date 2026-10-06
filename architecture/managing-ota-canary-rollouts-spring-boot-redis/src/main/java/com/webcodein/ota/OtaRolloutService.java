package com.webcodein.ota;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtaRolloutService {

    private final StringRedisTemplate redisTemplate;

    public OtaRolloutService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isEligibleForUpdate(long deviceId, String version) {
        Boolean result = redisTemplate.opsForValue().getBit("rollout:" + version, deviceId);
        return Boolean.TRUE.equals(result);
    }

    public void enableUpdateForDevice(long deviceId, String version) {
        redisTemplate.opsForValue().setBit("rollout:" + version, deviceId, true);
    }
}
