package com.webcodein.boundedcontext.catalog.dto;

import java.util.UUID;

public record ProductResponseDto(
    UUID id,
    String sku,
    String name,
    String description,
    double price
) {}
