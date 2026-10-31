package com.webcodein.shop.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserSummaryTest {

    @Test
    @DisplayName("Should verify UserSummary record component accessors and immutability")
    void recordComponentAccessors() {
        UserSummary summary = new UserSummary(42L, "johndoe", "john@example.com");

        assertThat(summary.id()).isEqualTo(42L);
        assertThat(summary.username()).isEqualTo("johndoe");
        assertThat(summary.email()).isEqualTo("john@example.com");

        UserSummary same = new UserSummary(42L, "johndoe", "john@example.com");
        assertThat(summary).isEqualTo(same);
        assertThat(summary.hashCode()).isEqualTo(same.hashCode());
        assertThat(summary.toString()).contains("42").contains("johndoe");
    }
}
