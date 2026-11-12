package com.webcodein.ecommerce.inventory;

import com.webcodein.ecommerce.order.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository, "InventoryRepository must not be null");
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

    @Transactional(readOnly = true)
    public Optional<Integer> getStock(String productCode) {
        if (productCode == null || productCode.isBlank()) {
            return Optional.empty();
        }
        return inventoryRepository.findById(productCode).map(InventoryItem::getStockQuantity);
    }

    @Transactional
    public void restock(String productCode, int quantity) {
        InventoryItem item = inventoryRepository.findById(productCode)
                .orElseGet(() -> new InventoryItem(productCode, 0));
        item.addStock(quantity);
        inventoryRepository.save(item);
        log.info("Restocked product: {}, new quantity: {}", productCode, item.getStockQuantity());
    }
}
