package com.webcodein.boundedcontext.catalog;

import com.webcodein.boundedcontext.catalog.dto.CreateProductRequest;
import com.webcodein.boundedcontext.catalog.dto.ProductResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @Test
    @DisplayName("POST /api/catalog/products should return 201 Created")
    void shouldCreateProduct() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponseDto responseDto = new ProductResponseDto(
            id, "SKU-TEST", "Test Item", "Description", 49.99
        );

        given(catalogService.createProduct(any(CreateProductRequest.class)))
            .willReturn(responseDto);

        mockMvc.perform(post("/api/catalog/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "sku": "SKU-TEST",
                        "name": "Test Item",
                        "description": "Description",
                        "price": 49.99
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.sku").value("SKU-TEST"))
            .andExpect(jsonPath("$.name").value("Test Item"))
            .andExpect(jsonPath("$.price").value(49.99));
    }

    @Test
    @DisplayName("GET /api/catalog/products/sku/{sku} should return product")
    void shouldGetProductBySku() throws Exception {
        UUID id = UUID.randomUUID();
        ProductResponseDto responseDto = new ProductResponseDto(
            id, "SKU-TEST", "Test Item", "Description", 49.99
        );

        given(catalogService.getProductBySku("SKU-TEST"))
            .willReturn(Optional.of(responseDto));

        mockMvc.perform(get("/api/catalog/products/sku/SKU-TEST"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sku").value("SKU-TEST"))
            .andExpect(jsonPath("$.name").value("Test Item"));
    }
}
