package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

public class InventoryService {
    public boolean canFulfillOrder(Product product, int requestedQuantity) {
        return product.isInStock() && product.getStockQuantity() >= requestedQuantity;
    }
}
