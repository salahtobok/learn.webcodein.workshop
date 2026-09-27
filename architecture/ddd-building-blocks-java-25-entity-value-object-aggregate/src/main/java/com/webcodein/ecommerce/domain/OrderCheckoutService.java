package com.webcodein.ecommerce.domain;

public class OrderCheckoutService {
    
    // The service requires multiple parts of the domain to do its job
    public void processCheckout(Order order, Customer customer) {
        if (!customer.isActive()) {
            throw new IllegalStateException("Inactive customers cannot checkout");
        }
        
        Money total = order.calculateTotalAmount();
        
        // Complex logic spanning multiple aggregates
        if (total.amount().compareTo(java.math.BigDecimal.valueOf(1000)) > 0) {
            // Apply high value verification process
            applySecurityVerification(order);
        }
        
        // Proceed with checkout...
    }
    
    private void applySecurityVerification(Order order) {
        // Implementation omitted
    }
}
