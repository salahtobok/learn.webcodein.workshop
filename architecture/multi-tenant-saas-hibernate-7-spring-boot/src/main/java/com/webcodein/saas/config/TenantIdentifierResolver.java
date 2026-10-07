package com.webcodein.saas.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        // Fallback to a default or system tenant if necessary, 
        // but for RLS we should return a clear 'unknown' to avoid data leaks.
        return tenant != null ? tenant : "UNKNOWN";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
