package com.webcodein.ddd;

import com.webcodein.ddd.domain.Address;
import com.webcodein.ddd.domain.Order;
import com.webcodein.ddd.domain.OrderItem;
import com.webcodein.ddd.infrastructure.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
class OrderIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveAndRetrieveAggregateRoot() {
        // Arrange: Create Value Object
        Address address = new Address("123 Main St", "Tech City", "10001");
        
        // Arrange: Create Aggregate Root
        Order order = new Order(address);
        
        // Arrange: Add Entity items
        order.addItem(new OrderItem("Laptop", 1, new BigDecimal("1500.00")));
        order.addItem(new OrderItem("Mouse", 2, new BigDecimal("25.00")));
        
        // Act: Save Aggregate
        orderRepository.save(order);
        
        // Act: Retrieve Aggregate
        Optional<Order> savedOrderOpt = orderRepository.findById(order.getId());
        
        // Assert
        assertThat(savedOrderOpt).isPresent();
        Order savedOrder = savedOrderOpt.get();
        assertThat(savedOrder.getStatus()).isEqualTo("PENDING");
        assertThat(savedOrder.getItems()).hasSize(2);
        assertThat(savedOrder.calculateTotal()).isEqualTo(new BigDecimal("1550.00"));
        assertThat(savedOrder.getShippingAddress().getCity()).isEqualTo("Tech City");
    }

    @Test
    void shouldFindOrdersByStatus() {
        Order order = new Order(new Address("456 Elm St", "Data Town", "99999"));
        order.addItem(new OrderItem("Keyboard", 1, new BigDecimal("100.00")));
        order.confirm(); // Changes status to CONFIRMED
        
        orderRepository.save(order);
        
        List<Order> confirmedOrders = orderRepository.findByStatus("CONFIRMED");
        assertThat(confirmedOrders).isNotEmpty();
        assertThat(confirmedOrders.get(0).getStatus()).isEqualTo("CONFIRMED");
    }
}
