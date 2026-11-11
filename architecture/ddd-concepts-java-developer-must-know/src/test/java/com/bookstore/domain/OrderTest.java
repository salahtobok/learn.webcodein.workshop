package com.bookstore.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("Entities must be equal when IDs match regardless of mutable state")
    void shouldEqualBasedOnIdentityOnly() {
        UUID sharedId = UUID.randomUUID();
        Order order1 = new Order(sharedId);
        Order order2 = new Order(sharedId);

        order2.complete(); // Changes state to COMPLETED

        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    @DisplayName("Entities with different IDs must not be equal")
    void shouldNotEqualWithDifferentIds() {
        Order order1 = new Order(UUID.randomUUID());
        Order order2 = new Order(UUID.randomUUID());

        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    @DisplayName("Order should start in PENDING state and transition to COMPLETED")
    void shouldHandleLifecycleTransitions() {
        Order order = new Order(UUID.randomUUID());
        assertThat(order.isReadyForCheckout()).isTrue();

        order.complete();
        assertThat(order.isReadyForCheckout()).isFalse();
    }

    @Test
    @DisplayName("Member placing order should enforce checkout readiness")
    void shouldEnforceReadinessWhenMemberPlacesOrder() {
        BookstoreMember member = new BookstoreMember(new MemberId("member-101"), true);
        Order pendingOrder = new Order(UUID.randomUUID());

        // Should succeed without exception
        member.placeOrder(pendingOrder);

        // When completed, should throw IllegalStateException
        pendingOrder.complete();
        assertThatThrownBy(() -> member.placeOrder(pendingOrder))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Order is not ready");
    }
}
