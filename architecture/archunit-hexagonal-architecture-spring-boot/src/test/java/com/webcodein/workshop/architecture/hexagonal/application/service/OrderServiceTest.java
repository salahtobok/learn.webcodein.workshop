package com.webcodein.workshop.architecture.hexagonal.application.service;

import com.webcodein.workshop.architecture.hexagonal.application.port.out.OrderRepositoryPort;
import com.webcodein.workshop.architecture.hexagonal.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepositoryPort);
    }

    @Test
    @DisplayName("Should save order through outbound port and return order ID")
    void shouldPlaceOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "Mechanical Keyboard", 1);

        UUID resultId = orderService.placeOrder(order);

        assertThat(resultId).isEqualTo(orderId);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepositoryPort).save(captor.capture());
        assertThat(captor.getValue()).isEqualTo(order);
    }

    @Test
    @DisplayName("Should retrieve order by ID through outbound port")
    void shouldRetrieveOrderById() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, "Ergonomic Chair", 1);
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrder(orderId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
        verify(orderRepositoryPort).findById(orderId);
    }

    @Test
    @DisplayName("Should return empty optional when order is not found")
    void shouldReturnEmptyWhenNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrder(orderId);

        assertThat(result).isEmpty();
        verify(orderRepositoryPort).findById(orderId);
    }
}
