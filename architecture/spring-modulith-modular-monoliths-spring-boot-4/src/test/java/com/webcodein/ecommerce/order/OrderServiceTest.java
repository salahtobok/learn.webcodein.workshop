package com.webcodein.ecommerce.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should save order and publish OrderPlacedEvent")
    void shouldPlaceOrderAndPublishEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            ReflectionTestUtils.setField(o, "id", 501L);
            return o;
        });

        Order placed = orderService.placeOrder("GPU-4090", 1);

        assertNotNull(placed);
        assertEquals(501L, placed.getId());
        assertEquals("GPU-4090", placed.getProductCode());
        assertEquals(1, placed.getQuantity());

        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        OrderPlacedEvent event = eventCaptor.getValue();
        assertEquals(501L, event.orderId());
        assertEquals("GPU-4090", event.productCode());
        assertEquals(1, event.quantity());
    }

    @Test
    @DisplayName("Should return order when ID exists in repository")
    void shouldGetOrderWhenExists() {
        Order mockOrder = new Order("CPU-CORE-I9", 2);
        ReflectionTestUtils.setField(mockOrder, "id", 777L);
        when(orderRepository.findById(777L)).thenReturn(Optional.of(mockOrder));

        Optional<Order> result = orderService.getOrder(777L);
        assertTrue(result.isPresent());
        assertEquals(777L, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional when order ID does not exist")
    void shouldReturnEmptyWhenNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrder(999L);
        assertTrue(result.isEmpty());
    }
}
