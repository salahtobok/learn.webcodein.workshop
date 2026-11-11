package com.webcodein.workshop.architecture.ddd;

import com.webcodein.workshop.architecture.ddd.application.AddItemCommand;
import com.webcodein.workshop.architecture.ddd.application.CreateOrderCommand;
import com.webcodein.workshop.architecture.ddd.application.OrderApplicationService;
import com.webcodein.workshop.architecture.ddd.application.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Spring Boot End-to-End Context & Persistence Integration Tests")
class BookstoreApplicationTests {

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Test
    @DisplayName("Context loads and executes full order lifecycle end-to-end")
    void shouldExecuteFullOrderLifecycle() {
        // 1. Create order
        OrderDto order = orderApplicationService.createOrder(new CreateOrderCommand("integration@example.com"));
        assertThat(order).isNotNull();
        assertThat(order.status()).isEqualTo("DRAFT");

        // 2. Add line items
        OrderDto updated = orderApplicationService.addItem(order.id(), new AddItemCommand(
                "BOOK-JAVA-25",
                2,
                new BigDecimal("49.99"),
                "USD"
        ));
        assertThat(updated.lines()).hasSize(1);
        assertThat(updated.totalAmount()).isEqualTo(new BigDecimal("99.98"));

        // 3. Confirm order
        OrderDto confirmed = orderApplicationService.confirmOrder(order.id());
        assertThat(confirmed.status()).isEqualTo("CONFIRMED");

        // 4. Retrieve order
        OrderDto retrieved = orderApplicationService.getOrder(order.id());
        assertThat(retrieved.status()).isEqualTo("CONFIRMED");
        assertThat(retrieved.lines()).hasSize(1);
    }
}
