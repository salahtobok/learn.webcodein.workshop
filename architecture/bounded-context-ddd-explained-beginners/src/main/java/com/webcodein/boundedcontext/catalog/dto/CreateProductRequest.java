package com.webcodein.boundedcontext.catalog.dto;

public record CreateProductRequest(
    String sku,
    String name,
    String description,
    double price
) {}
