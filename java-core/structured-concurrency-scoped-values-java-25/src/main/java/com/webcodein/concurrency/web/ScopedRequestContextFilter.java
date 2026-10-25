package com.webcodein.concurrency.web;

import com.webcodein.concurrency.context.RequestContextScope;
import com.webcodein.concurrency.model.RequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component("scopedRequestContextFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ScopedRequestContextFilter extends OncePerRequestFilter {

    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER_CORRELATION_ID);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String userId = request.getHeader(HEADER_USER_ID);
        if (userId == null || userId.isBlank()) {
            userId = "anonymous";
        }

        String tenantId = request.getHeader(HEADER_TENANT_ID);
        if (tenantId == null || tenantId.isBlank()) {
            tenantId = "tenant-primary";
        }

        RequestContext context = new RequestContext(
                correlationId,
                userId,
                tenantId,
                Instant.now()
        );

        response.setHeader(HEADER_CORRELATION_ID, correlationId);

        // Bind the ScopedValue for the duration of this HTTP request.
        // All downstream controller calls, service logic, and forked Virtual Threads
        // within StructuredTaskScope automatically inherit this immutable context.
        try {
            RequestContextScope.callWith(context, () -> {
                filterChain.doFilter(request, response);
                return null;
            });
        } catch (ServletException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException("Failed processing request within ScopedValue context", e);
        }
    }
}
