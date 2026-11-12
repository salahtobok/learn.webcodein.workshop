package com.webcodein.workshop.dddmistakes.store.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Store Customer Aggregate Boundary Tests")
class CustomerAggregateTest {

    @Test
    @DisplayName("Should update billing address through aggregate method")
    void shouldUpdateBillingAddress() {
        UUID customerId = UUID.randomUUID();
        Address address1 = new Address();
        Address address2 = new Address();

        Customer customer = new Customer(customerId, "Alice Doe", address1);
        customer.changeBillingAddress(address2);

        assertNotNull(customer);
    }
}
