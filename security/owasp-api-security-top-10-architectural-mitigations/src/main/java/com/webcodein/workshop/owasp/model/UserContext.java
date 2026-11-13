package com.webcodein.workshop.owasp.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Authenticated user identity and tenant context extracted from verified credentials.
 */
public record UserContext(String userId, String tenantId, Set<String> roles) {

    public UserContext {
        Objects.requireNonNull(userId, "UserId must not be null");
        Objects.requireNonNull(tenantId, "TenantId must not be null");
        roles = roles != null ? Collections.unmodifiableSet(Set.copyOf(roles)) : Set.of();
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean isSystemAdmin() {
        return roles.contains("ROLE_SYSTEM_ADMIN");
    }
}
