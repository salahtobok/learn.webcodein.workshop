package com.webcodein.workshop.mcp.service;

import com.webcodein.workshop.mcp.model.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {

    private final Map<String, InventoryItem> inventory = new ConcurrentHashMap<>();

    public InventoryService() {
        inventory.put("SKU-MACBOOK-16", InventoryItem.of("SKU-MACBOOK-16", "Apple MacBook Pro 16 M3 Max", 14, "WH-EAST-A1"));
        inventory.put("SKU-DELL-XPS15", InventoryItem.of("SKU-DELL-XPS15", "Dell XPS 15 OLED", 28, "WH-WEST-B2"));
        inventory.put("SKU-THINKPAD-P1", InventoryItem.of("SKU-THINKPAD-P1", "Lenovo ThinkPad P1 Gen 7", 5, "WH-CENTRAL-C4"));
        inventory.put("SKU-LOGITECH-MX3", InventoryItem.of("SKU-LOGITECH-MX3", "Logitech MX Master 3S Mouse", 120, "WH-EAST-D1"));
    }

    public Optional<InventoryItem> checkStock(String sku) {
        return Optional.ofNullable(inventory.get(sku));
    }

    public boolean reserveStock(String sku, int quantity) {
        return inventory.computeIfPresent(sku, (k, existing) -> {
            if (existing.stockQuantity() >= quantity) {
                int updated = existing.stockQuantity() - quantity;
                return new InventoryItem(existing.sku(), existing.name(), updated, existing.warehouseLocation(), updated > 0);
            }
            return existing;
        }) != null;
    }

    public Map<String, InventoryItem> getAllInventory() {
        return Map.copyOf(inventory);
    }
}
