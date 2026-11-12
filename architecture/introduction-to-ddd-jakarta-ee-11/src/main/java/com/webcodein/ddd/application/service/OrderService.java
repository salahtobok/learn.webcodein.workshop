package com.webcodein.ddd.application.service;

import com.webcodein.ddd.application.dto.AddressDto;
import com.webcodein.ddd.application.dto.CreateOrderRequest;
import com.webcodein.ddd.application.dto.OrderItemDto;
import com.webcodein.ddd.application.dto.OrderResponse;
import com.webcodein.ddd.domain.Address;
import com.webcodein.ddd.domain.Order;
import com.webcodein.ddd.domain.OrderItem;
import com.webcodein.ddd.infrastructure.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        Address address = new Address(
                request.shippingAddress().street(),
                request.shippingAddress().city(),
                request.shippingAddress().zipCode()
        );
        Order order = new Order(address);
        if (request.items() != null) {
            for (OrderItemDto itemDto : request.items()) {
                order.addItem(new OrderItem(itemDto.productName(), itemDto.quantity(), itemDto.price()));
            }
        }
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        return toResponse(order);
    }

    public OrderResponse confirmOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        order.confirm();
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        AddressDto addressDto = new AddressDto(
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getZipCode()
        );
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductName(), i.getQuantity(), i.getPrice()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.calculateTotal(),
                addressDto,
                itemDtos
        );
    }
}
