package com.webcodein.workshop.mcp;

import com.webcodein.workshop.mcp.service.CustomerLoyaltyService;
import com.webcodein.workshop.mcp.service.InventoryService;
import com.webcodein.workshop.mcp.tool.EnterpriseMcpTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnterpriseMcpToolsTest {

    private EnterpriseMcpTools mcpTools;

    @BeforeEach
    void setUp() {
        InventoryService inventoryService = new InventoryService();
        CustomerLoyaltyService customerLoyaltyService = new CustomerLoyaltyService();
        mcpTools = new EnterpriseMcpTools(inventoryService, customerLoyaltyService);
    }

    @Test
    @DisplayName("Should return formatted inventory response for known SKU")
    void shouldReturnFormattedStock() {
        String result = mcpTools.checkProductStock("SKU-MACBOOK-16");

        assertThat(result).contains("SKU: SKU-MACBOOK-16")
                .contains("Units Available: 14")
                .contains("WH-EAST-A1");
    }

    @Test
    @DisplayName("Should return not found message for unknown SKU")
    void shouldReturnNotFoundForUnknownSku() {
        String result = mcpTools.checkProductStock("UNKNOWN-SKU");

        assertThat(result).contains("was not found in enterprise warehouse system");
    }

    @Test
    @DisplayName("Should return customer tier benefits for valid customer")
    void shouldReturnCustomerTierBenefits() {
        String result = mcpTools.lookupCustomerTier("CUST-101");

        assertThat(result).contains("Customer: CUST-101")
                .contains("Tier: PLATINUM")
                .contains("Discount: 25.0%")
                .contains("VIP Support: true");
    }

    @Test
    @DisplayName("Should handle inventory reservation")
    void shouldHandleInventoryReservation() {
        String result = mcpTools.reserveWarehouseStock("SKU-MACBOOK-16", 1);

        assertThat(result).contains("Successfully reserved 1 units");
    }
}
