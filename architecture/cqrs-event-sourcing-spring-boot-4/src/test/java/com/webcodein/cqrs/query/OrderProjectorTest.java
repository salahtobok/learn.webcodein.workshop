package com.webcodein.cqrs.query;

import com.webcodein.cqrs.events.OrderConfirmedEvent;
import com.webcodein.cqrs.events.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProjectorTest {

    @Mock
    private OrderReadRepository repository;

    private OrderProjector projector;

    @BeforeEach
    void setUp() {
        projector = new OrderProjector(repository);
    }

    @Test
    @DisplayName("Unit Test: on(OrderCreatedEvent) projects new OrderReadModel with PENDING status")
    void testHandleOrderCreatedEvent() {
        UUID orderId = UUID.randomUUID();
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, "CUST-456", 299.0, Instant.now());

        projector.on(event);

        ArgumentCaptor<OrderReadModel> captor = ArgumentCaptor.forClass(OrderReadModel.class);
        verify(repository).save(captor.capture());

        OrderReadModel saved = captor.getValue();
        assertThat(saved.getId()).isEqualTo(orderId);
        assertThat(saved.getCustomerId()).isEqualTo("CUST-456");
        assertThat(saved.getAmount()).isEqualTo(299.0);
        assertThat(saved.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Unit Test: on(OrderConfirmedEvent) updates existing projection to CONFIRMED")
    void testHandleOrderConfirmedEvent() {
        UUID orderId = UUID.randomUUID();
        OrderReadModel existing = new OrderReadModel(orderId, "CUST-456", 299.0, "PENDING");
        when(repository.findById(orderId)).thenReturn(Optional.of(existing));

        OrderConfirmedEvent event = new OrderConfirmedEvent(orderId, Instant.now());
        projector.on(event);

        verify(repository).save(existing);
        assertThat(existing.getStatus()).isEqualTo("CONFIRMED");
    }
}
