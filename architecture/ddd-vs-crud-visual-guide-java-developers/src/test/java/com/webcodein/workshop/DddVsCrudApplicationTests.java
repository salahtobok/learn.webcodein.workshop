package com.webcodein.workshop;

import com.webcodein.workshop.crud.OrderEntity;
import com.webcodein.workshop.crud.OrderService;
import com.webcodein.workshop.ddd.application.CreateOrderCommand;
import com.webcodein.workshop.ddd.application.OrderApplicationService;
import com.webcodein.workshop.ddd.application.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("DDD vs CRUD Full Spring Boot Application Context Integration Tests")
class DddVsCrudApplicationTests {

    @Autowired
    private OrderService crudOrderService;

    @Autowired
    private OrderApplicationService dddOrderService;

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        assertNotNull(crudOrderService);
        assertNotNull(dddOrderService);
    }

    @Test
    @DisplayName("Verify side-by-side execution: CRUD service vs DDD application service")
    void shouldExecuteSideBySide() {
        // 1. Execute CRUD workflow
        OrderEntity crudOrder = crudOrderService.createOrder("crud.buyer@example.com", BigDecimal.valueOf(99.00));
        assertNotNull(crudOrder.getId());
        assertEquals("CREATED", crudOrder.getStatus());

        crudOrderService.fulfillOrder(crudOrder.getId());
        OrderEntity fulfilledCrud = crudOrderService.findById(crudOrder.getId());
        assertEquals("FULFILLED", fulfilledCrud.getStatus());

        // 2. Execute DDD workflow
        Long dddOrderId = dddOrderService.createOrder(new CreateOrderCommand("ddd.buyer@example.com", BigDecimal.valueOf(149.50), "USD"));
        assertNotNull(dddOrderId);

        OrderDto createdDdd = dddOrderService.getOrder(dddOrderId);
        assertEquals("CREATED", createdDdd.status());

        dddOrderService.fulfillOrder(dddOrderId);
        OrderDto fulfilledDdd = dddOrderService.getOrder(dddOrderId);
        assertEquals("FULFILLED", fulfilledDdd.status());
    }
}
