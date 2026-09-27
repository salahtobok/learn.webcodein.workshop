package com.webcodein.workshop.dddmistakes.logistics.domain;
public interface WarehouseRepository {
    Warehouse findNearestTo(Address address);
}