package com.bookstore.infrastructure;

import org.springframework.stereotype.Component;

@Component
public class MockLegacySoapShippingClient implements LegacySoapShippingClient {

    @Override
    public LegacyShippingResponse fetch(String isbn) {
        // Deterministic mock payload for simulation
        return new LegacyShippingResponse("1.85", "N");
    }
}
