package com.webcodein.workshop.ddd;

import com.webcodein.workshop.ddd.crud.Order;
import com.webcodein.workshop.ddd.crud.OrderRepository;
import com.webcodein.workshop.ddd.crud.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Legacy CRUD OrderService Tests")
class OrderServiceTest {

    private OrderRepository repository;
    private OrderService service;

    @BeforeEach
    void setUp() {
        repository = mock(OrderRepository.class);
        service = new OrderService(repository);
    }

    @Test
    @DisplayName("Should create new CRUD order with default CREATED status")
    void createOrder() {
        Order savedOrder = new Order();
        when(repository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = service.createOrder();
        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
    }

    @Test
    @DisplayName("Should add amount to CRUD order when in CREATED status")
    void addAmountToCreatedOrder() {
        Order order = new Order();
        UUID id = order.getId();
        when(repository.findById(id)).thenReturn(Optional.of(order));
        when(repository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        service.addAmount(id, BigDecimal.valueOf(50.00));

        assertEquals(BigDecimal.valueOf(50.00), order.getTotalAmount());
        verify(repository).save(order);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when adding amount to non-CREATED order")
    void rejectAddAmountOnShippedOrder() {
        Order order = new Order();
        order.setStatus("SHIPPED");
        UUID id = order.getId();
        when(repository.findById(id)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.addAmount(id, BigDecimal.valueOf(25.00))
        );
        assertTrue(ex.getMessage().contains("Order not in CREATED status"));
    }
}
