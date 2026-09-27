package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.application;

import java.math.BigDecimal;

public record CreateProductCommand(String name, BigDecimal price, String currency, int initialStock) {}
