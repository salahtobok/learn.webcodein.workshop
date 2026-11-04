package com.webcodein.boundedcontext.order;

import com.webcodein.boundedcontext.order.acl.CatalogClient;
import com.webcodein.boundedcontext.order.acl.ProductSnapshot;
import com.webcodein.boundedcontext.order.dto.CreateOrderRequest;
import com.webcodein.boundedcontext.order.dto.OrderItemRequest;
import com.webcodein.boundedcontext.order.dto.OrderItemResponseDto;
import com.webcodein.boundedcontext.order.dto.OrderResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    public OrderResponseDto createOrder(CreateOrderRequest request) {
        Order order = new Order(request.customerId());

        if (request.items() != null) {
            for (OrderItemRequest itemReq : request.items()) {
                ProductSnapshot product = catalogClient.fetchProductBySku(itemReq.sku())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found in Catalog for SKU: " + itemReq.sku()));

                OrderItem item = new OrderItem(
                    product.sku(),
                    product.name(),
                    product.price(),
                    itemReq.quantity()
                );
                order.addItem(item);
            }
        }

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    public OrderResponseDto confirmOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.confirmOrder();
        return toDto(order);
    }

    public OrderResponseDto cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.cancelOrder();
        return toDto(order);
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponseDto> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomerId(UUID customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
            .map(this::toDto)
            .toList();
    }

    private OrderResponseDto toDto(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
            .map(i -> new OrderItemResponseDto(
                i.getId(),
                i.getSku(),
                i.getProductName(),
                i.getUnitPrice(),
                i.getQuantity(),
                i.getSubtotal()
            ))
            .toList();

        return new OrderResponseDto(
            order.getId(),
            order.getCustomerId(),
            order.getStatus(),
            order.getCreatedAt(),
            order.calculateTotal(),
            itemDtos
        );
    }
}
