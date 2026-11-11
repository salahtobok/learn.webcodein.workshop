package com.webcodein.workshop.architecture.ddd.domain;

import com.webcodein.workshop.architecture.ddd.domain.event.OrderConfirmedEvent;
import com.webcodein.workshop.architecture.ddd.domain.event.OrderPlacedEvent;
import com.webcodein.workshop.architecture.ddd.domain.model.order.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DDD Aggregate Root: Order Invariant Tests")
class OrderTest {

    @Test
    @DisplayName("Should initialize order in DRAFT status and register OrderPlacedEvent")
    void shouldInitializeOrderCorrectly() {
        OrderId id = OrderId.generate();
        CustomerEmail email = new CustomerEmail("alice@example.com");

        Order order = new Order(id, email);

        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getCustomerEmail()).isEqualTo(email);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(order.getLines()).isEmpty();
        assertThat(order.getDomainEvents()).hasSize(1);
        assertThat(order.getDomainEvents().getFirst()).isInstanceOf(OrderPlacedEvent.class);
    }

    @Test
    @DisplayName("Should add items and calculate subtotal and total amount")
    void shouldAddItemsAndCalculateTotal() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("bob@example.com"));

        order.addLine(new BookId("BOOK-001"), Quantity.of(2), Money.of(15.00, "USD"));
        order.addLine(new BookId("BOOK-002"), Quantity.of(1), Money.of(25.00, "USD"));

        assertThat(order.getLines()).hasSize(2);
        assertThat(order.totalAmount().amount()).isEqualTo(new BigDecimal("55.00"));
        assertThat(order.totalAmount().currency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should increment quantity when adding an existing book item")
    void shouldIncrementQuantityForExistingBook() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("carol@example.com"));

        order.addLine(new BookId("BOOK-001"), Quantity.of(2), Money.of(20.00, "USD"));
        order.addLine(new BookId("BOOK-001"), Quantity.of(3), Money.of(20.00, "USD"));

        assertThat(order.getLines()).hasSize(1);
        assertThat(order.getLines().getFirst().getQuantity().value()).isEqualTo(5);
        assertThat(order.totalAmount().amount()).isEqualTo(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should reject confirming an empty order")
    void shouldRejectConfirmingEmptyOrder() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("dave@example.com"));

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot confirm an order without items");
    }

    @Test
    @DisplayName("Should successfully confirm non-empty draft order and record OrderConfirmedEvent")
    void shouldConfirmOrderSuccessfully() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("eve@example.com"));
        order.addLine(new BookId("BOOK-001"), Quantity.of(1), Money.of(30.00, "USD"));

        order.confirm();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getDomainEvents())
                .filteredOn(e -> e instanceof OrderConfirmedEvent)
                .hasSize(1);
    }

    @Test
    @DisplayName("Should prevent adding items to a confirmed order")
    void shouldPreventModifyingConfirmedOrder() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("frank@example.com"));
        order.addLine(new BookId("BOOK-001"), Quantity.of(1), Money.of(10.00, "USD"));
        order.confirm();

        assertThatThrownBy(() -> order.addLine(new BookId("BOOK-002"), Quantity.of(1), Money.of(15.00, "USD")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add items to an order with status: CONFIRMED");
    }

    @Test
    @DisplayName("Should prevent cancelling an already cancelled order")
    void shouldPreventCancellingTwice() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("grace@example.com"));
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already cancelled");
    }

    @Test
    @DisplayName("Should enforce maximum limit of 50 order lines")
    void shouldEnforceMaxOrderLines() {
        Order order = new Order(OrderId.generate(), new CustomerEmail("heidi@example.com"));

        for (int i = 1; i <= 50; i++) {
            order.addLine(new BookId("BOOK-" + i), Quantity.of(1), Money.of(10.00, "USD"));
        }

        assertThat(order.getLines()).hasSize(50);
        assertThatThrownBy(() -> order.addLine(new BookId("BOOK-51"), Quantity.of(1), Money.of(10.00, "USD")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot contain more than 50 distinct line items");
    }
}
