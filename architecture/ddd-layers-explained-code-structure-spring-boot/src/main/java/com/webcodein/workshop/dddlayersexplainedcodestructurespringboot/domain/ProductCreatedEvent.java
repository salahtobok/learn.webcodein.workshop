package com.webcodein.workshop.dddlayersexplainedcodestructurespringboot.domain;

import java.time.LocalDateTime;

public record ProductCreatedEvent(ProductId productId, ProductName name, LocalDateTime occurredAt) {}
