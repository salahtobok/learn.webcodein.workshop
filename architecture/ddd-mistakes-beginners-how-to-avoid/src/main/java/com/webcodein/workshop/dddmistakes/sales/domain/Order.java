package com.webcodein.workshop.dddmistakes.sales.domain;
import java.util.UUID;
import java.util.List;
import java.util.Collections;
public class Order {
    private UUID id;
    private PaymentStatus paymentStatus;
    public Order(UUID id) { this.id = id; }
    public UUID getId() { return id; }
    public List<Object> getItems() { return Collections.emptyList(); }
    public void confirm() {}
    public boolean readyToDispatch() {
        return this.paymentStatus == PaymentStatus.CLEARED;
    }
}