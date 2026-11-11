package com.webcodein.workshop.architecture.ddd.domain.model.order;

import com.webcodein.workshop.architecture.ddd.domain.event.OrderConfirmedEvent;
import com.webcodein.workshop.architecture.ddd.domain.event.OrderPlacedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Order {
    public static final int MAX_ORDER_LINES = 50;

    private final OrderId id;
    private final CustomerEmail customerEmail;
    private final List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status;
    private final Instant createdAt;
    private final List<Object> domainEvents = new ArrayList<>();

    public Order(OrderId id, CustomerEmail customerEmail) {
        this(id, customerEmail, OrderStatus.DRAFT, Instant.now());
        recordEvent(new OrderPlacedEvent(this.id, this.customerEmail, Instant.now()));
    }

    public Order(OrderId id, CustomerEmail customerEmail, OrderStatus status, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "OrderId cannot be null");
        this.customerEmail = Objects.requireNonNull(customerEmail, "CustomerEmail cannot be null");
        this.status = Objects.requireNonNull(status, "OrderStatus cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
    }

    public static Order reconstitute(
            OrderId id,
            CustomerEmail customerEmail,
            OrderStatus status,
            Instant createdAt,
            List<OrderLine> lines
    ) {
        Order order = new Order(id, customerEmail, status, createdAt);
        if (lines != null) {
            order.lines.addAll(lines);
        }
        return order;
    }

    public void addLine(BookId bookId, Quantity quantity, Money unitPrice) {
        assertNotLocked("Cannot add items to an order with status: " + status);

        Optional<OrderLine> existing = lines.stream()
                .filter(l -> l.getBookId().equals(bookId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
            return;
        }

        if (lines.size() >= MAX_ORDER_LINES) {
            throw new IllegalStateException("Order cannot contain more than " + MAX_ORDER_LINES + " distinct line items");
        }

        lines.add(OrderLine.create(bookId, quantity, unitPrice));
    }

    public void removeLine(BookId bookId) {
        assertNotLocked("Cannot remove items from an order with status: " + status);
        lines.removeIf(l -> l.getBookId().equals(bookId));
    }

    public void confirm() {
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be confirmed. Current status: " + status);
        }
        if (this.lines.isEmpty()) {
            throw new IllegalStateException("Cannot confirm an order without items");
        }

        this.status = OrderStatus.CONFIRMED;
        recordEvent(new OrderConfirmedEvent(this.id, totalAmount(), Instant.now()));
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public Money totalAmount() {
        if (lines.isEmpty()) {
            return Money.ZERO_USD;
        }
        Money total = new Money(java.math.BigDecimal.ZERO, lines.getFirst().getUnitPrice().currency());
        for (OrderLine line : lines) {
            total = total.add(line.subtotal());
        }
        return total;
    }

    private void assertNotLocked(String message) {
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException(message);
        }
    }

    public OrderId getId() {
        return id;
    }

    public CustomerEmail getCustomerEmail() {
        return customerEmail;
    }

    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    private void recordEvent(Object event) {
        this.domainEvents.add(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
