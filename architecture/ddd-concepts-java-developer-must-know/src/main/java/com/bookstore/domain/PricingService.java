package com.bookstore.domain;

import org.springframework.stereotype.Service;

@Service // Domain Service
public class PricingService {
    
    public Money calculateFinalPrice(BookstoreMember member, BookAggregate book) {
        Money basePrice = book.getPrice();
        
        if (member.isVip()) {
            // Apply 20% discount
            return basePrice.multiply(0.8);
        }
        
        if (book.isClearance()) {
            // Apply $5 flat discount
            return basePrice.subtract(new Money(new java.math.BigDecimal("5.00"), basePrice.currency()));
        }
        
        return basePrice;
    }
}
