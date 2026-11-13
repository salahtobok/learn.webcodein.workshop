package com.webcodein.workshop.owasp.engine;

import com.webcodein.workshop.owasp.model.AccountRecord;
import com.webcodein.workshop.owasp.model.SecurityDecision;
import com.webcodein.workshop.owasp.model.UserContext;

import java.util.Objects;

/**
 * Architectural defense against OWASP API1:2023 - Broken Object Level Authorization (BOLA).
 * Guarantees that users cannot access or mutate objects outside their ownership or tenant scope.
 */
public class BolaGuard {

    public SecurityDecision evaluateAccess(UserContext user, AccountRecord account) {
        Objects.requireNonNull(user, "User context must not be null");
        Objects.requireNonNull(account, "Account record must not be null");

        // 1. Cross-Tenant Isolation Enforcement
        if (!user.tenantId().equals(account.tenantId()) && !user.isSystemAdmin()) {
            return SecurityDecision.deny(
                "CROSS_TENANT_ACCESS_DENIED",
                "User tenant " + user.tenantId() + " cannot access resource of tenant " + account.tenantId()
            );
        }

        // 2. Object Ownership or Administrative Delegation Enforcement
        boolean isOwner = user.userId().equals(account.ownerId());
        boolean hasTenantAdmin = user.hasRole("ROLE_TENANT_ADMIN") && user.tenantId().equals(account.tenantId());
        boolean hasSystemAdmin = user.isSystemAdmin();

        if (isOwner || hasTenantAdmin || hasSystemAdmin) {
            return SecurityDecision.allow();
        }

        return SecurityDecision.deny(
            "BOLA_OBJECT_OWNERSHIP_VIOLATION",
            "User " + user.userId() + " does not own account " + account.accountId()
        );
    }
}
