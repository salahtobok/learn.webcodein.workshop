package com.webcodein.ddd.application.service;

import com.webcodein.ddd.domain.model.order.Order;
import com.webcodein.ddd.domain.model.order.OrderStatus;
import com.webcodein.ddd.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderApplicationService orderApplicationService;

    @BeforeEach
    void setUp() {
        orderApplicationService = new OrderApplicationService(orderRepository);
    }

    @Test
    @DisplayName("createOrder creates and persists new pending order")
    void createOrderSuccess() {
        UUID customerId = UUID.randomUUID();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID orderId = orderApplicationService.createOrder(customerId);

        assertNotNull(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("addProductToOrder adds item to existing order and saves")
    void addProductToOrderSuccess() {
        Order order = new Order(UUID.randomUUID());
        UUID orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UUID productId = UUID.randomUUID();
        orderApplicationService.addProductToOrder(orderId, productId, 2, 29.99);

        assertEquals(1, order.getItems().size());
        assertEquals(59.98, order.getTotalAmount(), 0.001);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("addProductToOrder throws IllegalArgumentException when order not found")
    void addProductToOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderApplicationService.addProductToOrder(orderId, UUID.randomUUID(), 1, 10.0));
    }

    @Test
    @DisplayName("confirmOrder confirms existing order with items and saves")
    void confirmOrderSuccess() {
        Order order = new Order(UUID.randomUUID());
        order.addItem(UUID.randomUUID(), 1, 20.0);
        UUID orderId = order.getId();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderApplicationService.confirmOrder(orderId);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("confirmOrder throws IllegalArgumentException when order not found")
    void confirmOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                orderApplicationService.confirmOrder(orderId));
    }
}
