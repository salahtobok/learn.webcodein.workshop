package com.webcodein.workshop.dddmistakes.sales.domain;
import java.util.UUID;
import java.util.List;
public record OrderConfirmedEvent(UUID orderId, List<Object> items) {}