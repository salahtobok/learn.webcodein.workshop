package com.webcodein.hexagonal.infrastructure;

import com.webcodein.hexagonal.domain.model.Money;
import com.webcodein.hexagonal.domain.model.Order;
import com.webcodein.hexagonal.domain.model.OrderId;
import com.webcodein.hexagonal.domain.port.in.CreateOrderUseCase;
import com.webcodein.hexagonal.infrastructure.adapter.in.rest.OrderResource;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderResource JAX-RS Inbound Adapter Tests")
class OrderResourceTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @InjectMocks
    private OrderResource orderResource;

    @Test
    @DisplayName("Should return 201 Created when order is successfully created")
    void shouldCreateOrderViaJaxRs() {
        OrderId orderId = new OrderId("ord-123");
        Order order = new Order(orderId, "cust-456", new Money(new BigDecimal("99.99")));

        when(createOrderUseCase.createOrder(eq("cust-456"), eq(new BigDecimal("99.99"))))
                .thenReturn(order);

        OrderResource.OrderRequest request = new OrderResource.OrderRequest("cust-456", new BigDecimal("99.99"));
        Response response = orderResource.createOrder(request);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        OrderResource.OrderResponse entity = (OrderResource.OrderResponse) response.getEntity();
        assertEquals("ord-123", entity.id());
        assertEquals("CREATED", entity.status());
        verify(createOrderUseCase).createOrder("cust-456", new BigDecimal("99.99"));
    }
}
