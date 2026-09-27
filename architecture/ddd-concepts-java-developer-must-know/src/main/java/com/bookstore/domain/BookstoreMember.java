package com.bookstore.domain;

public class BookstoreMember {
    private final MemberId memberId;
    private final boolean vip;
    
    public BookstoreMember(MemberId memberId, boolean vip) {
        this.memberId = memberId;
        this.vip = vip;
    }
    
    public boolean isVip() {
        return this.vip;
    }
    
    public void placeOrder(Order order) {
        if (!order.isReadyForCheckout()) {
            throw new IllegalStateException("Order is not ready");
        }
        // Process the order
    }
}
