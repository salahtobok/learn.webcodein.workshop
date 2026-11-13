package com.webcodein.workshop.mcp.model;

import java.util.Objects;

public record CustomerBenefit(
        String customerId,
        CustomerTier tier,
        double discountPercentage,
        boolean freeShippingEligible,
        boolean vipSupport
) {
    public CustomerBenefit {
        Objects.requireNonNull(customerId, "Customer ID must not be null");
        Objects.requireNonNull(tier, "Customer tier must not be null");
        if (discountPercentage < 0.0 || discountPercentage > 100.0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
    }

    public static CustomerBenefit forTier(String customerId, CustomerTier tier) {
        return switch (tier) {
            case BRONZE -> new CustomerBenefit(customerId, tier, 5.0, false, false);
            case SILVER -> new CustomerBenefit(customerId, tier, 10.0, true, false);
            case GOLD -> new CustomerBenefit(customerId, tier, 15.0, true, true);
            case PLATINUM -> new CustomerBenefit(customerId, tier, 25.0, true, true);
        };
    }
}
