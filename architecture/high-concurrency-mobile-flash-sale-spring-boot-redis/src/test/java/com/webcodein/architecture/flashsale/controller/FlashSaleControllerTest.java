package com.webcodein.architecture.flashsale.controller;

import com.webcodein.architecture.flashsale.service.FlashSaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FlashSaleControllerTest {

    @Mock
    private FlashSaleService flashSaleService;

    @InjectMocks
    private FlashSaleController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/flash-sale/{productId}/purchase should return HTTP 200 on successful purchase")
    void shouldReturnOkWhenPurchaseSucceeds() throws Exception {
        when(flashSaleService.purchase("IPHONE15", "user1")).thenReturn(true);

        mockMvc.perform(post("/api/flash-sale/IPHONE15/purchase")
                        .param("userId", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Purchase successful! Order is being processed."));
    }

    @Test
    @DisplayName("POST /api/flash-sale/{productId}/purchase should return HTTP 400 when product is sold out")
    void shouldReturnBadRequestWhenSoldOut() throws Exception {
        when(flashSaleService.purchase("IPHONE15", "user2")).thenReturn(false);

        mockMvc.perform(post("/api/flash-sale/IPHONE15/purchase")
                        .param("userId", "user2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Sold out!"));
    }
}
