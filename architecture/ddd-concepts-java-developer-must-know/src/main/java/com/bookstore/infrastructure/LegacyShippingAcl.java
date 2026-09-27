package com.bookstore.infrastructure;

import com.bookstore.domain.ShippingDetails;
import org.springframework.stereotype.Component;

@Component
public class LegacyShippingAcl {
    private final LegacySoapShippingClient legacyClient;
    
    public LegacyShippingAcl(LegacySoapShippingClient legacyClient) {
        this.legacyClient = legacyClient;
    }
    
    // Translates terrible legacy DTO into a beautiful Domain Object
    public ShippingDetails getShippingInfo(String isbn) {
        // Fetch legacy data
        LegacyShippingResponse response = legacyClient.fetch(isbn);
        
        // Translate and protect our domain
        double weight = Double.parseDouble(response.getWgt());
        boolean requiresSignature = response.getSigReq().equals("Y");
        
        return new ShippingDetails(weight, requiresSignature);
    }
}
