package com.webcodein.kafka;

import com.webcodein.kafka.controller.OrderController;
import com.webcodein.kafka.model.OrderEvent;
import com.webcodein.kafka.service.OrderConsumerService;
import com.webcodein.kafka.service.OrderProducerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderProducerService producerService;

    @Mock
    private OrderConsumerService consumerService;

    @InjectMocks
    private OrderController orderController;

    @Test
    @DisplayName("Should create order via producer service")
    void shouldCreateOrderViaApi() {
        OrderEvent mockEvent = new OrderEvent("ord-123", "CREATED", 150.0);
        when(producerService.sendOrder(150.0)).thenReturn(mockEvent);

        OrderEvent response = orderController.createOrder(150.0);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo("ord-123");
        assertThat(response.getStatus()).isEqualTo("CREATED");
        assertThat(response.getAmount()).isEqualTo(150.0);
        verify(producerService).sendOrder(150.0);
    }

    @Test
    @DisplayName("Should list processed orders from consumer service")
    void shouldListProcessedOrders() {
        OrderEvent event = new OrderEvent("ord-456", "PROCESSED", 75.5);
        when(consumerService.getProcessedOrders()).thenReturn(List.of(event));

        List<OrderEvent> response = orderController.getProcessedOrders();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getOrderId()).isEqualTo("ord-456");
        assertThat(response.getFirst().getAmount()).isEqualTo(75.5);
        verify(consumerService).getProcessedOrders();
    }
}
