package com.webcodein.workshop.mcp;

import com.webcodein.workshop.mcp.model.CustomerBenefit;
import com.webcodein.workshop.mcp.model.CustomerTier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerBenefitTest {

    @Test
    @DisplayName("Should create Platinum tier benefits with 25% discount and VIP support")
    void shouldCreatePlatinumTierBenefits() {
        CustomerBenefit benefit = CustomerBenefit.forTier("CUST-999", CustomerTier.PLATINUM);

        assertThat(benefit.customerId()).isEqualTo("CUST-999");
        assertThat(benefit.tier()).isEqualTo(CustomerTier.PLATINUM);
        assertThat(benefit.discountPercentage()).isEqualTo(25.0);
        assertThat(benefit.freeShippingEligible()).isTrue();
        assertThat(benefit.vipSupport()).isTrue();
    }

    @Test
    @DisplayName("Should throw exception for negative discount percentage")
    void shouldThrowExceptionForInvalidDiscount() {
        assertThatThrownBy(() -> new CustomerBenefit("CUST-1", CustomerTier.BRONZE, -5.0, false, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Discount percentage must be between 0 and 100");
    }

    @Test
    @DisplayName("Should create Bronze tier with standard benefits")
    void shouldCreateBronzeTierBenefits() {
        CustomerBenefit benefit = CustomerBenefit.forTier("CUST-001", CustomerTier.BRONZE);

        assertThat(benefit.discountPercentage()).isEqualTo(5.0);
        assertThat(benefit.freeShippingEligible()).isFalse();
        assertThat(benefit.vipSupport()).isFalse();
    }
}
