package com.webcodein.workshop.ddd.application;

import com.webcodein.workshop.ddd.domain.event.OrderPlacedEvent;
import com.webcodein.workshop.ddd.domain.event.OrderShippedEvent;
import com.webcodein.workshop.ddd.domain.model.CustomerId;
import com.webcodein.workshop.ddd.domain.model.Money;
import com.webcodein.workshop.ddd.domain.model.Order;
import com.webcodein.workshop.ddd.domain.model.OrderId;
import com.webcodein.workshop.ddd.domain.model.OrderStatus;
import com.webcodein.workshop.ddd.infrastructure.persistence.InMemoryOrderDomainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderApplicationServiceTest {

    private InMemoryOrderDomainRepository repository;
    private ApplicationEventPublisher eventPublisher;
    private OrderApplicationService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderDomainRepository();
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new OrderApplicationService(repository, eventPublisher);
    }

    @Test
    @DisplayName("Should create order, save aggregate, and publish OrderPlacedEvent")
    void createOrderSuccess() {
        CustomerId customerId = CustomerId.generate();
        OrderId orderId = service.createOrder(
                customerId,
                "USD",
                "LAPTOP-01",
                Money.of(1200.00, "USD"),
                1
        );

        assertNotNull(orderId);
        Order order = service.getOrder(orderId);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(Money.of(1200.00, "USD"), order.calculateTotal());

        ArgumentCaptor<OrderPlacedEvent> captor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(eventPublisher, times(1)).publishEvent(captor.capture());
        OrderPlacedEvent event = captor.getValue();
        assertEquals(orderId, event.orderId());
        assertEquals(Money.of(1200.00, "USD"), event.totalAmount());
    }

    @Test
    @DisplayName("Should apply discount to existing order")
    void applyDiscountSuccess() {
        OrderId orderId = service.createOrder(
                CustomerId.generate(),
                "USD",
                "DESK-01",
                Money.of(300.00, "USD"),
                1
        );

        service.applyDiscount(orderId, Money.of(50.00, "USD"));

        Order order = service.getOrder(orderId);
        assertEquals(Money.of(250.00, "USD"), order.calculateTotal());
        assertEquals(Money.of(50.00, "USD"), order.getDiscount());
    }

    @Test
    @DisplayName("Should ship order and publish OrderShippedEvent")
    void shipOrderSuccess() {
        OrderId orderId = service.createOrder(
                CustomerId.generate(),
                "USD",
                "KEYBOARD-01",
                Money.of(100.00, "USD"),
                1
        );

        service.shipOrder(orderId);

        Order order = service.getOrder(orderId);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        verify(eventPublisher, times(1)).publishEvent(isA(OrderShippedEvent.class));
    }

    @Test
    @DisplayName("Should throw NoSuchElementException for non-existent order ID")
    void nonExistentOrderThrows() {
        OrderId nonExistent = OrderId.generate();
        assertThrows(NoSuchElementException.class, () -> service.getOrder(nonExistent));
    }
}
