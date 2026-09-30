package com.webcodein.ecommerce.inventory;

import com.webcodein.ecommerce.order.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @ApplicationModuleListener
    void on(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order: {}", event.orderId());
        
        inventoryRepository.findById(event.productCode()).ifPresent(item -> {
            item.reduceStock(event.quantity());
            inventoryRepository.save(item);
            log.info("Reduced stock for product: {}, new quantity: {}", event.productCode(), item.getStockQuantity());
        });
    }
}
