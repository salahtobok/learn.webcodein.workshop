package com.webcodein.cqrs.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderQueryController.class)
class OrderQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @Test
    void shouldReturnCustomerOrders() throws Exception {
        OrderSummaryView view1 = new OrderSummaryView(1L, "CUST-700", 120.00, "Pending Fulfillment");
        OrderSummaryView view2 = new OrderSummaryView(2L, "CUST-700", 240.00, "Pending Fulfillment");

        when(orderQueryService.getCustomerOrders("CUST-700")).thenReturn(List.of(view1, view2));

        mockMvc.perform(get("/api/orders/summary/CUST-700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(120.00))
                .andExpect(jsonPath("$[1].orderId").value(2))
                .andExpect(jsonPath("$[1].totalAmount").value(240.00));

        verify(orderQueryService).getCustomerOrders("CUST-700");
    }
}
