package com.webcodein.workshop.dddmistakes.logistics.domain;
public class Order {
    public boolean isReadyForFulfillment() { return true; }
    public Address getShippingAddress() { return new Address(); }
    public java.util.List<Object> getItems() { return java.util.Collections.emptyList(); }
    public void markAsDispatched() {}
}