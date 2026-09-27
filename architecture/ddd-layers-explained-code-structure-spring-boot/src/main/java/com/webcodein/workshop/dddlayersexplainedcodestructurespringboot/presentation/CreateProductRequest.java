package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.presentation;

import java.math.BigDecimal;

// Request DTO — Presentation layer only
public record CreateProductRequest(String name, BigDecimal price, String currency, int initialStock) {}
