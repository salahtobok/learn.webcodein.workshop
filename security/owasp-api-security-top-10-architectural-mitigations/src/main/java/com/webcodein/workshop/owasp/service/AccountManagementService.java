package com.webcodein.workshop.owasp.service;

import com.webcodein.workshop.owasp.engine.BolaGuard;
import com.webcodein.workshop.owasp.engine.BoplaInspector;
import com.webcodein.workshop.owasp.model.AccountRecord;
import com.webcodein.workshop.owasp.model.SecurityDecision;
import com.webcodein.workshop.owasp.model.UpdateAccountRequest;
import com.webcodein.workshop.owasp.model.UserContext;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise service coordinating data access with active OWASP API security boundaries.
 */
public class AccountManagementService {

    private final Map<String, AccountRecord> accountStore = new ConcurrentHashMap<>();
    private final BolaGuard bolaGuard = new BolaGuard();
    private final BoplaInspector boplaInspector = new BoplaInspector();

    public AccountManagementService() {
        // Seed initial accounts across different tenants
        seedAccount(new AccountRecord("ACC_1001", "TENANT_ALPHA", "USER_ALICE", new BigDecimal("15000.00"), "TIER_PLATINUM", "alice@alpha.corp"));
        seedAccount(new AccountRecord("ACC_1002", "TENANT_ALPHA", "USER_BOB", new BigDecimal("450.00"), "TIER_STANDARD", "bob@alpha.corp"));
        seedAccount(new AccountRecord("ACC_2001", "TENANT_BETA", "USER_CHARLIE", new BigDecimal("250000.00"), "TIER_ENTERPRISE", "charlie@beta.corp"));
    }

    public void seedAccount(AccountRecord account) {
        accountStore.put(account.accountId(), account);
    }

    /**
     * BOLA-Protected Account Retrieval.
     * Prevents any user from reading accounts they do not own or belong to another tenant.
     */
    public Optional<AccountRecord> getAccount(UserContext user, String accountId) {
        AccountRecord account = accountStore.get(accountId);
        if (account == null) {
            return Optional.empty();
        }

        SecurityDecision decision = bolaGuard.evaluateAccess(user, account);
        if (!decision.allowed()) {
            throw new SecurityException("BOLA Authorization Failure: " + decision.reason());
        }

        return Optional.of(account);
    }

    /**
     * BOLA- and BOPLA-Protected Account Update.
     * Validates ownership before mutation and enforces strict property whitelisting against Mass Assignment.
     */
    public AccountRecord updateAccount(UserContext user, String accountId, String rawJsonUpdate) {
        AccountRecord account = accountStore.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        // 1. Enforce BOLA authorization check
        SecurityDecision bolaDecision = bolaGuard.evaluateAccess(user, account);
        if (!bolaDecision.allowed()) {
            throw new SecurityException("BOLA Authorization Failure: " + bolaDecision.reason());
        }

        // 2. Enforce BOPLA mass-assignment inspection and DTO binding
        try {
            UpdateAccountRequest request = boplaInspector.parseStrictDto(rawJsonUpdate);
            AccountRecord updated = account.withUpdatedEmail(request.email());
            accountStore.put(accountId, updated);
            return updated;
        } catch (Exception e) {
            throw new SecurityException("BOPLA Validation Failure: " + e.getMessage(), e);
        }
    }
}
