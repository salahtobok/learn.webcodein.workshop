package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.presentation;

import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application.CreateProductCommand;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application.ProductApplicationService;
import com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductApplicationService productService;

    @Test
    @DisplayName("POST /api/products should map request DTO to command and return 201 Created")
    void shouldCreateProductViaApi() throws Exception {
        UUID expectedId = UUID.randomUUID();
        when(productService.createProduct(any(CreateProductCommand.class)))
            .thenReturn(new ProductId(expectedId));

        String requestJson = """
            {
                "name": "Noise Cancelling Headphones",
                "price": 249.99,
                "currency": "USD",
                "initialStock": 50
            }
            """;

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(expectedId.toString()))
            .andExpect(jsonPath("$.message").value("Product created"));
    }
}
