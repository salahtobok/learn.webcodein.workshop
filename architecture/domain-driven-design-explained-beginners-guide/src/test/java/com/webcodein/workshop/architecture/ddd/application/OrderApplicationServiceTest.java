package com.webcodein.workshop.architecture.ddd.application;

import com.webcodein.workshop.architecture.ddd.domain.event.OrderConfirmedEvent;
import com.webcodein.workshop.architecture.ddd.domain.event.OrderPlacedEvent;
import com.webcodein.workshop.architecture.ddd.domain.model.order.*;
import com.webcodein.workshop.architecture.ddd.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DDD Application Layer: OrderApplicationService Tests")
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new OrderApplicationService(orderRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should create order, persist in repository, and publish OrderPlacedEvent")
    void shouldCreateOrderAndPublishEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDto dto = applicationService.createOrder(new CreateOrderCommand("user@example.com"));

        assertThat(dto).isNotNull();
        assertThat(dto.customerEmail()).isEqualTo("user@example.com");
        assertThat(dto.status()).isEqualTo("DRAFT");

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publishEvent(any(OrderPlacedEvent.class));
    }

    @Test
    @DisplayName("Should add item to order and persist changes")
    void shouldAddItemToOrder() {
        OrderId orderId = OrderId.generate();
        Order existing = new Order(orderId, new CustomerEmail("user@example.com"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        AddItemCommand cmd = new AddItemCommand("BOOK-100", 2, new BigDecimal("19.99"), "USD");
        OrderDto dto = applicationService.addItem(orderId.value(), cmd);

        assertThat(dto.lines()).hasSize(1);
        assertThat(dto.totalAmount()).isEqualTo(new BigDecimal("39.98"));
        verify(orderRepository).save(existing);
    }

    @Test
    @DisplayName("Should confirm order and publish OrderConfirmedEvent")
    void shouldConfirmOrderAndPublishEvent() {
        OrderId orderId = OrderId.generate();
        Order existing = new Order(orderId, new CustomerEmail("user@example.com"));
        existing.addLine(new BookId("BOOK-100"), Quantity.of(1), Money.of(25.00, "USD"));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderDto dto = applicationService.confirmOrder(orderId.value());

        assertThat(dto.status()).isEqualTo("CONFIRMED");
        verify(eventPublisher).publishEvent(any(OrderConfirmedEvent.class));
    }
}
