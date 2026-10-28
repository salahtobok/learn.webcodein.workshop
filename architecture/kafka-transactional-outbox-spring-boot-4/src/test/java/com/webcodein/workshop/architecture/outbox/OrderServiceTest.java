package com.webcodein.workshop.architecture.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webcodein.workshop.architecture.outbox.dto.OrderRequest;
import com.webcodein.workshop.architecture.outbox.entity.Order;
import com.webcodein.workshop.architecture.outbox.entity.OrderStatus;
import com.webcodein.workshop.architecture.outbox.entity.OutboxEvent;
import com.webcodein.workshop.architecture.outbox.repository.OrderRepository;
import com.webcodein.workshop.architecture.outbox.repository.OutboxEventRepository;
import com.webcodein.workshop.architecture.outbox.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

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
    @DisplayName("Unit Test: createOrder saves Order and corresponding OutboxEvent")
    void testCreateOrder_Success() throws Exception {
        UUID generatedId = UUID.randomUUID();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(generatedId);
            return o;
        });

        when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"" + generatedId + "\"}");

        OrderRequest request = new OrderRequest(
                "cust-123",
                "PROD-PHONE-Y",
                2,
                new BigDecimal("899.50")
        );

        Order result = orderService.createOrder(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(generatedId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getProductCode()).isEqualTo("PROD-PHONE-Y");

        verify(orderRepository).save(any(Order.class));

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        OutboxEvent capturedEvent = outboxCaptor.getValue();
        assertThat(capturedEvent.getAggregateType()).isEqualTo("Order");
        assertThat(capturedEvent.getAggregateId()).isEqualTo(generatedId.toString());
        assertThat(capturedEvent.getEventType()).isEqualTo("OrderCreated");
        assertThat(capturedEvent.isProcessed()).isFalse();
    }

    @Test
    @DisplayName("Unit Test: JSON serialization failure throws exception and aborts outbox save")
    void testCreateOrder_SerializationFailure() throws Exception {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("Serialization error") {});

        OrderRequest request = new OrderRequest(
                "cust-fail",
                "PROD-ERR",
                1,
                BigDecimal.TEN
        );

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to serialize order event payload");

        verify(outboxEventRepository, never()).save(any());
    }
}
