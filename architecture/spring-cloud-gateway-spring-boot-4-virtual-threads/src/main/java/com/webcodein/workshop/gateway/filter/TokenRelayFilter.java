package com.webcodein.workshop.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class TokenRelayFilter extends OncePerRequestFilter {

    public static final String RELAYED_TOKEN_ATTRIBUTE = "RELAYED_AUTH_TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            request.setAttribute(RELAYED_TOKEN_ATTRIBUTE, token);
            response.setHeader("X-Gateway-Identity-Relayed", "true");
        }

        filterChain.doFilter(request, response);
    }
}
