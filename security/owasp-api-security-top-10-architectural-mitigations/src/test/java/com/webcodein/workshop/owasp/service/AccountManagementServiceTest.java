package com.webcodein.workshop.owasp.service;

import com.webcodein.workshop.owasp.model.AccountRecord;
import com.webcodein.workshop.owasp.model.UserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountManagementServiceTest {

    private final AccountManagementService service = new AccountManagementService();

    @Test
    @DisplayName("Should successfully retrieve account when caller is legitimate owner")
    void shouldAllowOwnerAccess() {
        UserContext alice = new UserContext("USER_ALICE", "TENANT_ALPHA", Set.of("ROLE_USER"));
        Optional<AccountRecord> account = service.getAccount(alice, "ACC_1001");

        assertThat(account).isPresent();
        assertThat(account.get().accountId()).isEqualTo("ACC_1001");
        assertThat(account.get().email()).isEqualTo("alice@alpha.corp");
    }

    @Test
    @DisplayName("BOLA Defense: Should block caller trying to access another user's account")
    void shouldBlockBolaAttempt() {
        UserContext bob = new UserContext("USER_BOB", "TENANT_ALPHA", Set.of("ROLE_USER"));

        assertThatThrownBy(() -> service.getAccount(bob, "ACC_1001"))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("BOLA Authorization Failure");
    }

    @Test
    @DisplayName("BOPLA Defense: Should block mutation trying to tamper with account balance")
    void shouldBlockBoplaTamperAttempt() {
        UserContext alice = new UserContext("USER_ALICE", "TENANT_ALPHA", Set.of("ROLE_USER"));
        String payload = "{\"email\":\"alice_hacked@alpha.corp\",\"balance\":\"99999999.00\"}";

        assertThatThrownBy(() -> service.updateAccount(alice, "ACC_1001", payload))
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("BOPLA Validation Failure");
    }

    @Test
    @DisplayName("Should successfully update account when caller is owner and payload is valid")
    void shouldUpdateAccountSuccessfully() {
        UserContext alice = new UserContext("USER_ALICE", "TENANT_ALPHA", Set.of("ROLE_USER"));
        String payload = "{\"email\":\"alice_new@alpha.corp\",\"displayName\":\"Alice In Tech\"}";

        AccountRecord updated = service.updateAccount(alice, "ACC_1001", payload);
        assertThat(updated.email()).isEqualTo("alice_new@alpha.corp");
    }
}
