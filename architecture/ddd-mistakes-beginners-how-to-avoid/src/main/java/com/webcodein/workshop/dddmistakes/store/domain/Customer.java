package com.webcodein.workshop.dddmistakes.store.domain;
import java.util.UUID;
public class Customer {
    private UUID customerId;
    private String name;
    private Address billingAddress;
    public Customer(UUID customerId, String name, Address billingAddress) {
        this.customerId = customerId;
        this.name = name;
        this.billingAddress = billingAddress;
    }
    public void changeBillingAddress(Address newAddress) {
        this.billingAddress = newAddress;
    }
}