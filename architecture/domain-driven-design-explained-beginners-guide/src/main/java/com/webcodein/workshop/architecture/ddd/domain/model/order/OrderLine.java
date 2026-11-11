package com.webcodein.workshop.architecture.ddd.domain.model.order;

import java.util.Objects;
import java.util.UUID;

public class OrderLine {
    private final UUID id;
    private final BookId bookId;
    private Quantity quantity;
    private final Money unitPrice;

    public OrderLine(UUID id, BookId bookId, Quantity quantity, Money unitPrice) {
        this.id = Objects.requireNonNull(id, "OrderLine id cannot be null");
        this.bookId = Objects.requireNonNull(bookId, "BookId cannot be null");
        this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "UnitPrice cannot be null");
    }

    public static OrderLine create(BookId bookId, Quantity quantity, Money unitPrice) {
        return new OrderLine(UUID.randomUUID(), bookId, quantity, unitPrice);
    }

    public static OrderLine reconstitute(UUID id, BookId bookId, Quantity quantity, Money unitPrice) {
        return new OrderLine(id, bookId, quantity, unitPrice);
    }

    public UUID getId() {
        return id;
    }

    public BookId getBookId() {
        return bookId;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity.value());
    }

    void increaseQuantity(Quantity additional) {
        Objects.requireNonNull(additional, "Additional quantity cannot be null");
        this.quantity = this.quantity.add(additional);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLine orderLine = (OrderLine) o;
        return Objects.equals(id, orderLine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
