package com.bookstore.domain;

import org.springframework.stereotype.Service;

@Service
public class PromotionService {
    public void applyPromotion(BookAggregate myBook) {
        Specification<BookAggregate> promotable = new IsBestsellerSpec().and(new IsInStockSpec());
        if (promotable.isSatisfiedBy(myBook)) {
            myBook.applyDiscount();
        }
    }
}
