package com.webcodein.workshop.dddmistakes.sales.application;

import com.webcodein.workshop.dddmistakes.sales.domain.Order;
import com.webcodein.workshop.dddmistakes.sales.domain.OrderConfirmedEvent;
import com.webcodein.workshop.dddmistakes.sales.domain.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderCheckoutService Application Service Tests")
class OrderCheckoutServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderCheckoutService checkoutService;

    @Test
    @DisplayName("Should confirm order, persist changes, and publish OrderConfirmedEvent")
    void shouldCheckoutAndPublishEvent() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(order));

        checkoutService.checkout(orderId);

        verify(orderRepo).save(order);

        ArgumentCaptor<OrderConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(OrderConfirmedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        OrderConfirmedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(orderId, publishedEvent.orderId());
    }
}
