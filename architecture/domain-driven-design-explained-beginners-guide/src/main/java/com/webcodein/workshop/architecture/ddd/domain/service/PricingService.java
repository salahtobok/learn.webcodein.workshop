package com.webcodein.workshop.architecture.ddd.domain.service;

import com.webcodein.workshop.architecture.ddd.domain.model.order.Money;
import com.webcodein.workshop.architecture.ddd.domain.model.order.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class PricingService {
    private static final BigDecimal VIP_DISCOUNT_PERCENT = new BigDecimal("0.10"); // 10% discount
    private static final Money VIP_THRESHOLD = Money.of("100.00", "USD");

    public Money calculateDiscount(Order order, boolean isVipCustomer) {
        Objects.requireNonNull(order, "Order cannot be null");

        Money total = order.totalAmount();
        if (isVipCustomer && total.isGreaterThan(VIP_THRESHOLD)) {
            return total.multiply(VIP_DISCOUNT_PERCENT);
        }

        return new Money(BigDecimal.ZERO, total.currency());
    }

    public Money calculateFinalTotal(Order order, boolean isVipCustomer) {
        Money total = order.totalAmount();
        Money discount = calculateDiscount(order, isVipCustomer);
        return new Money(total.amount().subtract(discount.amount()), total.currency());
    }
}
