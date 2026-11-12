package com.webcodein.ddd.application.service;

import com.webcodein.ddd.application.dto.AddressDto;
import com.webcodein.ddd.application.dto.CreateOrderRequest;
import com.webcodein.ddd.application.dto.OrderItemDto;
import com.webcodein.ddd.application.dto.OrderResponse;
import com.webcodein.ddd.domain.Address;
import com.webcodein.ddd.domain.Order;
import com.webcodein.ddd.domain.OrderItem;
import com.webcodein.ddd.infrastructure.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Should create and persist order aggregate from request")
    void shouldCreateOrderFromRequest() {
        AddressDto addressDto = new AddressDto("123 Tech Ave", "Boston", "02101");
        List<OrderItemDto> itemDtos = List.of(
                new OrderItemDto("Ergonomic Chair", 1, new BigDecimal("350.00")),
                new OrderItemDto("Desk Mat", 1, new BigDecimal("30.00"))
        );
        CreateOrderRequest request = new CreateOrderRequest(addressDto, itemDtos);

        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.totalAmount()).isEqualTo(new BigDecimal("380.00"));
        assertThat(response.items()).hasSize(2);
        assertThat(response.shippingAddress().city()).isEqualTo("Boston");
    }

    @Test
    @DisplayName("Should return mapped order when found by ID")
    void shouldReturnOrderWhenFound() {
        Order order = new Order(new Address("789 Pine Rd", "Seattle", "98101"));
        order.addItem(new OrderItem("Microphone", 1, new BigDecimal("150.00")));

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(order.getId());

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(order.getId());
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.totalAmount()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Should throw exception when order ID is not found")
    void shouldThrowWhenOrderNotFound() {
        UUID randomId = UUID.randomUUID();
        given(orderRepository.findById(randomId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(randomId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order not found with id: " + randomId);
    }
}
