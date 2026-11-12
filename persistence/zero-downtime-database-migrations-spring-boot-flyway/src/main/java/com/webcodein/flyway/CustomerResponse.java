package com.webcodein.flyway;

public record CustomerResponse(Long id, String email, String fullName) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getEmail(), customer.getFullName());
    }
}
