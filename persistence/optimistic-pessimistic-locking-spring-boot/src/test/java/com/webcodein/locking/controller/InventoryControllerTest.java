package com.webcodein.locking.controller;

import com.webcodein.locking.entity.Product;
import com.webcodein.locking.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@DisplayName("InventoryController REST API Tests")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    @DisplayName("POST /api/inventory/products/{id}/decrement-optimistic returns 200 OK with updated product")
    void decrementOptimisticSuccess() throws Exception {
        Product product = new Product("Smart Watch", 45);
        product.setId(1L);
        product.setVersion(1L);

        when(inventoryService.decrementStockOptimistic(1L, 5)).thenReturn(product);

        mockMvc.perform(post("/api/inventory/products/1/decrement-optimistic")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Smart Watch"))
                .andExpect(jsonPath("$.stock").value(45))
                .andExpect(jsonPath("$.version").value(1L));
    }

    @Test
    @DisplayName("POST /api/inventory/products/{id}/decrement-optimistic-retry returns 200 OK with retried product")
    void decrementOptimisticRetrySuccess() throws Exception {
        Product product = new Product("Bluetooth Speaker", 28);
        product.setId(2L);
        product.setVersion(3L);

        when(inventoryService.decrementStockOptimisticWithRetry(2L, 2, 5)).thenReturn(product);

        mockMvc.perform(post("/api/inventory/products/2/decrement-optimistic-retry")
                        .param("quantity", "2")
                        .param("retries", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Bluetooth Speaker"))
                .andExpect(jsonPath("$.stock").value(28))
                .andExpect(jsonPath("$.version").value(3L));
    }

    @Test
    @DisplayName("POST /api/inventory/products/{id}/decrement-pessimistic returns 200 OK with locked update")
    void decrementPessimisticSuccess() throws Exception {
        Product product = new Product("4K Monitor", 10);
        product.setId(3L);
        product.setVersion(2L);

        when(inventoryService.decrementStockPessimistic(3L, 1)).thenReturn(product);

        mockMvc.perform(post("/api/inventory/products/3/decrement-pessimistic")
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("4K Monitor"))
                .andExpect(jsonPath("$.stock").value(10))
                .andExpect(jsonPath("$.version").value(2L));
    }
}
