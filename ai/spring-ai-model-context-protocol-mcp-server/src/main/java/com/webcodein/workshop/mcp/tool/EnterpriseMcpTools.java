package com.webcodein.workshop.mcp.tool;

import com.webcodein.workshop.mcp.model.CustomerBenefit;
import com.webcodein.workshop.mcp.model.InventoryItem;
import com.webcodein.workshop.mcp.service.CustomerLoyaltyService;
import com.webcodein.workshop.mcp.service.InventoryService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnterpriseMcpTools {

    private final InventoryService inventoryService;
    private final CustomerLoyaltyService customerLoyaltyService;

    public EnterpriseMcpTools(InventoryService inventoryService, CustomerLoyaltyService customerLoyaltyService) {
        this.inventoryService = inventoryService;
        this.customerLoyaltyService = customerLoyaltyService;
    }

    @Tool(description = "Check real-time warehouse inventory and stock availability by SKU")
    public String checkProductStock(String sku) {
        return inventoryService.checkStock(sku)
                .map(item -> "SKU: " + item.sku() + " | Item: " + item.name() + " | Units Available: " + item.stockQuantity() + " | Warehouse: " + item.warehouseLocation())
                .orElse("Item with SKU '" + sku + "' was not found in enterprise warehouse system.");
    }

    @Tool(description = "Lookup enterprise customer loyalty tier benefits, discount rates, and VIP support eligibility")
    public String lookupCustomerTier(String customerId) {
        return customerLoyaltyService.getCustomerBenefit(customerId)
                .map(benefit -> "Customer: " + benefit.customerId() + " | Tier: " + benefit.tier() + " | Discount: " + benefit.discountPercentage() + "% | Free Shipping: " + benefit.freeShippingEligible() + " | VIP Support: " + benefit.vipSupport())
                .orElse("Customer ID '" + customerId + "' has no registered loyalty profile.");
    }

    @Tool(description = "Reserve a specific quantity of inventory stock for a validated customer order")
    public String reserveWarehouseStock(String sku, int quantity) {
        if (quantity <= 0) {
            return "Reservation failed: quantity must be greater than zero.";
        }
        boolean success = inventoryService.reserveStock(sku, quantity);
        if (success) {
            return "Successfully reserved " + quantity + " units of SKU '" + sku + "'.";
        }
        return "Reservation failed: Insufficient stock or SKU '" + sku + "' not found.";
    }
}
