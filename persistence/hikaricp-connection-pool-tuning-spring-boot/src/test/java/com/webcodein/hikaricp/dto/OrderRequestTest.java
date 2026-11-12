package com.webcodein.hikaricp.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderRequestTest {

    @Test
    @DisplayName("Should create valid order request when parameters are valid")
    void shouldCreateValidOrderRequest() {
        OrderRequest request = new OrderRequest("Alice Walker", "PROD-101", new BigDecimal("149.99"));

        assertThat(request.customerName()).isEqualTo("Alice Walker");
        assertThat(request.productCode()).isEqualTo("PROD-101");
        assertThat(request.amount()).isEqualTo(new BigDecimal("149.99"));
    }

    @Test
    @DisplayName("Should reject order request with null or blank customer name")
    void shouldRejectInvalidCustomerName() {
        assertThatThrownBy(() -> new OrderRequest(null, "PROD-101", new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer name cannot be null or blank");

        assertThatThrownBy(() -> new OrderRequest("   ", "PROD-101", new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer name cannot be null or blank");
    }

    @Test
    @DisplayName("Should reject order request with null or blank product code")
    void shouldRejectInvalidProductCode() {
        assertThatThrownBy(() -> new OrderRequest("Alice", null, new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product code cannot be null or blank");

        assertThatThrownBy(() -> new OrderRequest("Alice", "", new BigDecimal("100.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product code cannot be null or blank");
    }

    @Test
    @DisplayName("Should reject order request with non-positive amount")
    void shouldRejectNonPositiveAmount() {
        assertThatThrownBy(() -> new OrderRequest("Alice", "PROD-101", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than zero");

        assertThatThrownBy(() -> new OrderRequest("Alice", "PROD-101", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than zero");

        assertThatThrownBy(() -> new OrderRequest("Alice", "PROD-101", new BigDecimal("-10.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount must be greater than zero");
    }
}
