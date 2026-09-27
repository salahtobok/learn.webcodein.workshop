package com.webcodein.workshop.dddmistakes.logistics.domain;
public class OrderFulfillmentService {
    private final WarehouseRepository warehouseRepository;
    private final LogisticsProvider logisticsProvider;
    public OrderFulfillmentService(WarehouseRepository warehouse, LogisticsProvider logistics) {
        this.warehouseRepository = warehouse;
        this.logisticsProvider = logistics;
    }
    public void dispatchOrderForDelivery(Order order) {
        if (order.isReadyForFulfillment()) {
            Warehouse warehouse = warehouseRepository.findNearestTo(order.getShippingAddress());
            DeliveryManifest manifest = warehouse.allocateInventory(order.getItems());
            logisticsProvider.schedulePickup(manifest);
            order.markAsDispatched();
        }
    }
}