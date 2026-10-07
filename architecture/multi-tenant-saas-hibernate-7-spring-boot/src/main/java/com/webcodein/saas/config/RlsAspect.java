package com.webcodein.saas.config;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RlsAspect {

    private final EntityManager entityManager;

    public RlsAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Intercept all Spring transactions to inject the RLS variable
    @Before("@annotation(org.springframework.transaction.annotation.Transactional) || @within(org.springframework.transaction.annotation.Transactional)")
    public void applyRowLevelSecurity() {
        String tenantId = TenantContext.getCurrentTenant();
        
        if (tenantId != null && !tenantId.equals("UNKNOWN")) {
            // Using Hibernate's unwrap to reliably execute the SET LOCAL command on the transaction's connection.
            entityManager.unwrap(Session.class).doWork(connection -> {
                try (var stmt = connection.createStatement()) {
                    stmt.execute("SET LOCAL app.current_tenant = '" + tenantId + "'");
                }
            });
        }
    }
}
