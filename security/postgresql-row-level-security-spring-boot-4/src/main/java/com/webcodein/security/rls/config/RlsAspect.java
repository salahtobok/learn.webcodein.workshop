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
            String tenantId = authentication.getName();
            entityManager.createNativeQuery("SET LOCAL ROLE app_user").executeUpdate();
            entityManager.createNativeQuery("SET LOCAL rls.tenant_id = '" + tenantId + "'").executeUpdate();
        }
    }
}
