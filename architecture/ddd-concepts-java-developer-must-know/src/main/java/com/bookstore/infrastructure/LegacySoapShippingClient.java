package com.bookstore.infrastructure;

public interface LegacySoapShippingClient {
    LegacyShippingResponse fetch(String isbn);
}
