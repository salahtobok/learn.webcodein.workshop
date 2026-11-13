package com.webcodein.workshop.owasp.engine;

import com.webcodein.workshop.owasp.model.AccountRecord;
import com.webcodein.workshop.owasp.model.SecurityDecision;
import com.webcodein.workshop.owasp.model.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BolaGuardTest {

    private final BolaGuard guard = new BolaGuard();

    @Test
    @DisplayName("Should allow owner to access their own account within same tenant")
    void shouldAllowOwnerAccess() {
        UserContext alice = new UserContext("USER_ALICE", "TENANT_1", Set.of("ROLE_USER"));
        AccountRecord record = new AccountRecord("ACC_1", "TENANT_1", "USER_ALICE", new BigDecimal("100"), "STANDARD", "alice@test.com");

        SecurityDecision decision = guard.evaluateAccess(alice, record);
        assertThat(decision.allowed()).isTrue();
    }

    @Test
    @DisplayName("BOLA: Should deny user from accessing another user's account even in same tenant")
    void shouldDenyUnauthorizedUserInSameTenant() {
        UserContext bob = new UserContext("USER_BOB", "TENANT_1", Set.of("ROLE_USER"));
        AccountRecord record = new AccountRecord("ACC_1", "TENANT_1", "USER_ALICE", new BigDecimal("100"), "STANDARD", "alice@test.com");

        SecurityDecision decision = guard.evaluateAccess(bob, record);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.violationType()).isEqualTo("BOLA_OBJECT_OWNERSHIP_VIOLATION");
    }

    @Test
    @DisplayName("BOLA / Multi-Tenant: Should deny cross-tenant access regardless of user ID")
    void shouldDenyCrossTenantAccess() {
        UserContext attacker = new UserContext("USER_ALICE", "TENANT_2", Set.of("ROLE_USER"));
        AccountRecord record = new AccountRecord("ACC_1", "TENANT_1", "USER_ALICE", new BigDecimal("100"), "STANDARD", "alice@test.com");

        SecurityDecision decision = guard.evaluateAccess(attacker, record);
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.violationType()).isEqualTo("CROSS_TENANT_ACCESS_DENIED");
    }

    @Test
    @DisplayName("Delegation: Should allow tenant admin to access tenant accounts")
    void shouldAllowTenantAdminAccess() {
        UserContext admin = new UserContext("ADMIN_1", "TENANT_1", Set.of("ROLE_TENANT_ADMIN"));
        AccountRecord record = new AccountRecord("ACC_1", "TENANT_1", "USER_ALICE", new BigDecimal("100"), "STANDARD", "alice@test.com");

        SecurityDecision decision = guard.evaluateAccess(admin, record);
        assertThat(decision.allowed()).isTrue();
    }
}
