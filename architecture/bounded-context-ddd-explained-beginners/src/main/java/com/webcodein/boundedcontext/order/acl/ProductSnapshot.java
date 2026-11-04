package com.webcodein.boundedcontext.order.acl;

public record ProductSnapshot(
    String sku,
    String name,
    double price
) {}
