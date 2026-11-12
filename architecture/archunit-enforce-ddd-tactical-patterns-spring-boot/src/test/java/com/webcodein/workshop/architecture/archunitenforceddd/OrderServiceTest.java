package com.webcodein.workshop.architecture.archunitenforceddd;

import com.webcodein.workshop.architecture.archunitenforceddd.application.service.OrderService;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Money;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.Order;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.model.OrderLine;
import com.webcodein.workshop.architecture.archunitenforceddd.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Application Service Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository);
    }

    @Test
    @DisplayName("Should create order with line and save to repository")
    void shouldCreateOrder() {
        Order order = orderService.createOrder("CUST-1", "ITEM-1", new BigDecimal("75.00"), "USD");

        assertNotNull(order);
        assertEquals("CUST-1", order.getCustomerId());
        assertEquals(Order.OrderStatus.CREATED, order.getStatus());
        assertEquals(1, order.getLines().size());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(order.getId(), captor.getValue().getId());
    }

    @Test
    @DisplayName("Should pay existing order and save state change")
    void shouldPayOrder() {
        UUID orderId = UUID.randomUUID();
        Order existing = new Order(orderId, "CUST-1");
        existing.addLine(new OrderLine(UUID.randomUUID(), "ITEM-1", new Money(new BigDecimal("20.00"), "USD")));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));

        Order result = orderService.payOrder(orderId);

        assertEquals(Order.OrderStatus.PAID, result.getStatus());
        verify(orderRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw exception when paying non-existent order")
    void shouldThrowWhenNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.payOrder(orderId));
    }
}
