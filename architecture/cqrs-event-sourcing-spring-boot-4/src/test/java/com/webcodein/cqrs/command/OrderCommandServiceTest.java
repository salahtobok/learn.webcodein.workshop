package com.webcodein.cqrs.command;

import com.webcodein.cqrs.core.EventStorePublisher;
import com.webcodein.cqrs.events.OrderConfirmedEvent;
import com.webcodein.cqrs.events.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {

    @Mock
    private EventStorePublisher eventPublisher;

    private OrderCommandService commandService;

    @BeforeEach
    void setUp() {
        commandService = new OrderCommandService(eventPublisher);
    }

    @Test
    @DisplayName("Unit Test: createOrder generates UUID and saves OrderCreatedEvent")
    void testCreateOrder() {
        String customerId = "CUST-901";
        double amount = 149.95;

        UUID orderId = commandService.createOrder(customerId, amount);

        assertThat(orderId).isNotNull();

        ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).saveAndPublish(captor.capture());

        OrderCreatedEvent event = captor.getValue();
        assertThat(event.aggregateId()).isEqualTo(orderId);
        assertThat(event.customerId()).isEqualTo(customerId);
        assertThat(event.amount()).isEqualTo(amount);
        assertThat(event.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Unit Test: confirmOrder publishes OrderConfirmedEvent for existing order ID")
    void testConfirmOrder() {
        UUID orderId = UUID.randomUUID();

        commandService.confirmOrder(orderId);

        ArgumentCaptor<OrderConfirmedEvent> captor = ArgumentCaptor.forClass(OrderConfirmedEvent.class);
        verify(eventPublisher).saveAndPublish(captor.capture());

        OrderConfirmedEvent event = captor.getValue();
        assertThat(event.aggregateId()).isEqualTo(orderId);
        assertThat(event.timestamp()).isNotNull();
    }
}
