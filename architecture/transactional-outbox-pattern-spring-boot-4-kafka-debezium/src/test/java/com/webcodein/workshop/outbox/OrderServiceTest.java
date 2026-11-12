package com.webcodein.workshop.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, outboxEventRepository, objectMapper);
    }

    @Test
    @DisplayName("Should save order and outbox event when placing an order")
    void shouldSaveOrderAndOutboxEvent() throws Exception {
        Order savedOrder = new Order(42L, "Keyboard", new BigDecimal("129.99"));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(savedOrder)).thenReturn("{\"id\":42,\"product\":\"Keyboard\",\"price\":129.99}");

        Order result = orderService.placeOrder("Keyboard", new BigDecimal("129.99"));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(42L);
        assertThat(result.getProduct()).isEqualTo("Keyboard");

        ArgumentCaptor<OutboxEvent> eventCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(eventCaptor.capture());

        OutboxEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getAggregateType()).isEqualTo("Order");
        assertThat(capturedEvent.getAggregateId()).isEqualTo("42");
        assertThat(capturedEvent.getEventType()).isEqualTo("OrderCreated");
        assertThat(capturedEvent.getPayload()).contains("\"id\":42");
    }

    @Test
    @DisplayName("Should throw RuntimeException when JSON serialization fails")
    void shouldThrowExceptionWhenSerializationFails() throws Exception {
        Order savedOrder = new Order(10L, "Mouse", new BigDecimal("49.99"));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        assertThatThrownBy(() -> orderService.placeOrder("Mouse", new BigDecimal("49.99")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to serialize order");

        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }
}
