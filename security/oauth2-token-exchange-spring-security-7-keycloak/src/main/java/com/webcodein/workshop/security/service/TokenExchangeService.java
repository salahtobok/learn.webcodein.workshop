package com.webcodein.workshop.security.service;

import com.webcodein.workshop.security.model.SecurityAuditRecord;
import com.webcodein.workshop.security.model.TokenExchangeRequest;
import com.webcodein.workshop.security.model.TokenExchangeResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TokenExchangeService {

    private final Map<String, SecurityAuditRecord> auditRecords = new ConcurrentHashMap<>();
    private final AtomicLong exchangeCounter = new AtomicLong(0);

    public TokenExchangeResponse exchangeToken(TokenExchangeRequest request) {
        exchangeCounter.incrementAndGet();

        // 1. Validate subject token
        String subject = extractSubject(request.subjectToken());
        String actor = request.actorToken() != null ? extractSubject(request.actorToken()) : "none (impersonation)";

        // 2. Privilege Attenuation (Downscoping): allow only requested scope if valid
        String grantedScope = determineGrantedScope(request.scope());

        // 3. Generate downscoped exchanged token simulation
        String exchangedToken = "exchanged_jwt_" + UUID.randomUUID().toString().substring(0, 12);

        // 4. Record audit log
        String auditId = "AUDIT-" + UUID.randomUUID().toString().substring(0, 8);
        SecurityAuditRecord audit = new SecurityAuditRecord(
                auditId,
                subject,
                actor,
                request.audience(),
                grantedScope,
                Instant.now()
        );
        auditRecords.put(auditId, audit);

        return new TokenExchangeResponse(
                exchangedToken,
                TokenExchangeRequest.TOKEN_TYPE_ACCESS_TOKEN,
                "Bearer",
                300, // 5 minutes TTL for zero-trust microservice hop
                grantedScope
        );
    }

    private String extractSubject(String token) {
        if (token.contains("alice")) {
            return "user:alice";
        } else if (token.contains("bob")) {
            return "user:bob";
        } else if (token.contains("gateway")) {
            return "service:api-gateway";
        } else if (token.contains("orders")) {
            return "service:orders-service";
        }
        return "user:standard";
    }

    private String determineGrantedScope(String requestedScope) {
        Set<String> allowedScopes = Set.of("read", "write", "payments:transfer", "orders:create", "reports:read");
        String[] requested = requestedScope.split(" ");
        List<String> validScopes = new ArrayList<>();
        for (String s : requested) {
            if (allowedScopes.contains(s.trim())) {
                validScopes.add(s.trim());
            }
        }
        return validScopes.isEmpty() ? "read" : String.join(" ", validScopes);
    }

    public List<SecurityAuditRecord> getAllAuditRecords() {
        return Collections.unmodifiableList(new ArrayList<>(auditRecords.values()));
    }

    public long getTotalExchanges() {
        return exchangeCounter.get();
    }
}
