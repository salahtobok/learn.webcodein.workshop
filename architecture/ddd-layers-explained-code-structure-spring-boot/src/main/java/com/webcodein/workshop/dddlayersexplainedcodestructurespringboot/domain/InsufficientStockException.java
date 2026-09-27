package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(ProductId id, int requested, int available) {
        super("Insufficient stock for product " + id.value() + ". Requested: " + requested + ", Available: " + available);
    }
}
