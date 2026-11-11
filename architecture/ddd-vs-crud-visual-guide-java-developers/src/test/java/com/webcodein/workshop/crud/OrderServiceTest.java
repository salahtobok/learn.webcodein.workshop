package com.webcodein.workshop.crud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CRUD Pattern: OrderService Anemic Tests")
class OrderServiceTest {

    private OrderRepository repository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(OrderRepository.class);
        orderService = new OrderService(repository);
    }

    @Test
    @DisplayName("Should create order with CREATED status")
    void shouldCreateOrder() {
        when(repository.save(any(OrderEntity.class))).thenAnswer(inv -> {
            OrderEntity e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        OrderEntity created = orderService.createOrder("user@example.com", BigDecimal.valueOf(99.99));

        assertNotNull(created);
        assertEquals(10L, created.getId());
        assertEquals("user@example.com", created.getCustomerEmail());
        assertEquals("CREATED", created.getStatus());
        verify(repository).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Should reject invalid email")
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder("invalid-email", BigDecimal.valueOf(50.0));
        });
    }

    @Test
    @DisplayName("Should reject non-positive amount")
    void shouldRejectNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder("user@example.com", BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should fulfill order in CREATED state")
    void shouldFulfillOrder() {
        OrderEntity order = new OrderEntity("user@example.com", BigDecimal.valueOf(50.0), "CREATED");
        order.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        orderService.fulfillOrder(1L);

        assertEquals("FULFILLED", order.getStatus());
        verify(repository).save(order);
    }

    @Test
    @DisplayName("Should throw when fulfilling non-created order")
    void shouldThrowWhenFulfillingNonCreatedOrder() {
        OrderEntity order = new OrderEntity("user@example.com", BigDecimal.valueOf(50.0), "CANCELLED");
        order.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.fulfillOrder(1L));
    }

    @Test
    @DisplayName("Demonstrate Anemic Vulnerability: Any code can mutate entity state directly bypassing service")
    void shouldDemonstrateAnemicVulnerabilityDirectSetterMutation() {
        OrderEntity anemic = new OrderEntity("user@example.com", BigDecimal.valueOf(50.0), "CREATED");

        // The entity has no defense against invalid state changes!
        anemic.setStatus("RANDOM_CORRUPTED_STATUS");
        anemic.setTotalAmount(BigDecimal.valueOf(-9999.0));

        assertEquals("RANDOM_CORRUPTED_STATUS", anemic.getStatus());
        assertEquals(BigDecimal.valueOf(-9999.0), anemic.getTotalAmount());
    }
}
