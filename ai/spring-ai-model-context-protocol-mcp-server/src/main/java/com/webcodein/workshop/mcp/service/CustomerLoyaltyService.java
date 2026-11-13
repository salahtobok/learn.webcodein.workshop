package com.webcodein.workshop.mcp.service;

import com.webcodein.workshop.mcp.model.CustomerBenefit;
import com.webcodein.workshop.mcp.model.CustomerTier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerLoyaltyService {

    private final Map<String, CustomerBenefit> customerTiers = new ConcurrentHashMap<>();

    public CustomerLoyaltyService() {
        customerTiers.put("CUST-101", CustomerBenefit.forTier("CUST-101", CustomerTier.PLATINUM));
        customerTiers.put("CUST-202", CustomerBenefit.forTier("CUST-202", CustomerTier.GOLD));
        customerTiers.put("CUST-303", CustomerBenefit.forTier("CUST-303", CustomerTier.SILVER));
        customerTiers.put("CUST-404", CustomerBenefit.forTier("CUST-404", CustomerTier.BRONZE));
    }

    public Optional<CustomerBenefit> getCustomerBenefit(String customerId) {
        return Optional.ofNullable(customerTiers.get(customerId));
    }

    public CustomerBenefit registerCustomerTier(String customerId, CustomerTier tier) {
        CustomerBenefit benefit = CustomerBenefit.forTier(customerId, tier);
        customerTiers.put(customerId, benefit);
        return benefit;
    }
}
