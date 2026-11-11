package com.webcodein.workshop.ddd.application;

import com.webcodein.workshop.ddd.domain.Email;
import com.webcodein.workshop.ddd.domain.Money;
import com.webcodein.workshop.ddd.domain.Order;
import com.webcodein.workshop.ddd.domain.OrderRepository;
import com.webcodein.workshop.ddd.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("DDD Pattern: OrderApplicationService Tests")
class OrderApplicationServiceTest {

    private OrderRepository repository;
    private OrderApplicationService appService;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(OrderRepository.class);
        appService = new OrderApplicationService(repository);
    }

    @Test
    @DisplayName("Should create order via command")
    void shouldCreateOrder() {
        when(repository.save(any(Order.class))).thenAnswer(inv -> {
            Order order = inv.getArgument(0);
            return Order.reconstitute(100L, order.getCustomerEmail(), order.getTotalAmount(), order.getStatus());
        });

        CreateOrderCommand cmd = new CreateOrderCommand("buyer@domain.com", BigDecimal.valueOf(75.0), "USD");
        Long id = appService.createOrder(cmd);

        assertEquals(100L, id);
        verify(repository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should fulfill order")
    void shouldFulfillOrder() {
        Order order = Order.reconstitute(
                1L,
                new Email("buyer@domain.com"),
                Money.usd(BigDecimal.valueOf(100.0)),
                OrderStatus.CREATED
        );
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        appService.fulfillOrder(1L);

        assertEquals(OrderStatus.FULFILLED, order.getStatus());
        verify(repository).save(order);
    }

    @Test
    @DisplayName("Should cancel order")
    void shouldCancelOrder() {
        Order order = Order.reconstitute(
                1L,
                new Email("buyer@domain.com"),
                Money.usd(BigDecimal.valueOf(100.0)),
                OrderStatus.CREATED
        );
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        appService.cancelOrder(1L, "Found better price");

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(repository).save(order);
    }

    @Test
    @DisplayName("Should query order and return DTO")
    void shouldGetOrderDto() {
        Order order = Order.reconstitute(
                5L,
                new Email("buyer@domain.com"),
                Money.usd(BigDecimal.valueOf(200.0)),
                OrderStatus.CREATED
        );
        when(repository.findById(5L)).thenReturn(Optional.of(order));

        OrderDto dto = appService.getOrder(5L);

        assertEquals(5L, dto.id());
        assertEquals("buyer@domain.com", dto.customerEmail());
        assertEquals(BigDecimal.valueOf(200.0), dto.totalAmount());
        assertEquals("USD", dto.currency());
        assertEquals("CREATED", dto.status());
    }
}
