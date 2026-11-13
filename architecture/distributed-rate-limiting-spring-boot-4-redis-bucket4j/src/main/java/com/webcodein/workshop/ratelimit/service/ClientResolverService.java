package com.webcodein.workshop.ratelimit.service;

import com.webcodein.workshop.ratelimit.model.RateLimitTier;
import org.springframework.stereotype.Service;

@Service
public class ClientResolverService {

    public RateLimitTier resolveTier(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return RateLimitTier.FREE;
        }
        if (apiKey.startsWith("ent_")) {
            return RateLimitTier.ENTERPRISE;
        }
        if (apiKey.startsWith("prem_")) {
            return RateLimitTier.PREMIUM;
        }
        if (apiKey.startsWith("basic_")) {
            return RateLimitTier.BASIC;
        }
        return RateLimitTier.FREE;
    }

    public String resolveClientKey(String apiKey, String clientIp) {
        if (apiKey != null && !apiKey.isBlank()) {
            return "api_key:" + apiKey;
        }
        return "ip:" + (clientIp != null ? clientIp : "127.0.0.1");
    }
}
