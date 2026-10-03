package com.webcodein.security.rls.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Aspect
@Component
public class RlsAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.webcodein.security.rls.repository.*.*(..))")
    public void setTenantContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // For simplicity, we use the username as the tenant ID. 
            // In a real SaaS, you would extract a custom claim (e.g., 'tenant_id') from a JWT token.
            String tenantId = authentication.getName();
            
            // Set the PostgreSQL session variable. RLS policies will read this.
            entityManager.createNativeQuery("SET LOCAL rls.tenant_id = '" + tenantId + "'").executeUpdate();
        }
    }
}
