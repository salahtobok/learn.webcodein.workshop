package com.webcodein.workshop.owasp.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserContextTest {

    @Test
    @DisplayName("Should create valid UserContext and evaluate roles correctly")
    void shouldCreateValidUserContext() {
        UserContext ctx = new UserContext("usr_1", "ten_a", Set.of("ROLE_USER", "ROLE_TENANT_ADMIN"));
        assertThat(ctx.userId()).isEqualTo("usr_1");
        assertThat(ctx.tenantId()).isEqualTo("ten_a");
        assertThat(ctx.hasRole("ROLE_USER")).isTrue();
        assertThat(ctx.hasRole("ROLE_SYSTEM_ADMIN")).isFalse();
        assertThat(ctx.isSystemAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should reject null userId or tenantId")
    void shouldRejectNulls() {
        assertThatThrownBy(() -> new UserContext(null, "ten", Set.of()))
            .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new UserContext("usr", null, Set.of()))
            .isInstanceOf(NullPointerException.class);
    }
}
