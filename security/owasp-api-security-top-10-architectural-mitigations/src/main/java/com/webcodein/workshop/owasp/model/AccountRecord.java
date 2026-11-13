package com.webcodein.workshop.owasp.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain record representing an enterprise financial account.
 */
public record AccountRecord(
    String accountId,
    String tenantId,
    String ownerId,
    BigDecimal balance,
    String tier,
    String email
) {
    public AccountRecord {
        Objects.requireNonNull(accountId, "AccountId must not be null");
        Objects.requireNonNull(tenantId, "TenantId must not be null");
        Objects.requireNonNull(ownerId, "OwnerId must not be null");
        Objects.requireNonNull(balance, "Balance must not be null");
        Objects.requireNonNull(tier, "Tier must not be null");
        Objects.requireNonNull(email, "Email must not be null");
    }

    public AccountRecord withUpdatedEmail(String newEmail) {
        return new AccountRecord(accountId, tenantId, ownerId, balance, tier, newEmail);
    }
}
