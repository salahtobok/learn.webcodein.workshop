package com.webcodein.workshop.security.controller;

import com.webcodein.workshop.security.model.SecurityAuditRecord;
import com.webcodein.workshop.security.model.TokenExchangeRequest;
import com.webcodein.workshop.security.model.TokenExchangeResponse;
import com.webcodein.workshop.security.service.TokenExchangeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class TokenExchangeController {

    private final TokenExchangeService tokenExchangeService;

    public TokenExchangeController(TokenExchangeService tokenExchangeService) {
        this.tokenExchangeService = tokenExchangeService;
    }

    @PostMapping("/oauth/token")
    public ResponseEntity<TokenExchangeResponse> exchangeToken(@Valid @RequestBody TokenExchangeRequest request) {
        TokenExchangeResponse response = tokenExchangeService.exchangeToken(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/token-exchange/audit")
    public ResponseEntity<List<SecurityAuditRecord>> getAuditRecords() {
        return ResponseEntity.ok(tokenExchangeService.getAllAuditRecords());
    }

    @GetMapping("/admin/token-exchange/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of(
                "totalTokenExchanges", tokenExchangeService.getTotalExchanges(),
                "status", "ACTIVE",
                "standard", "RFC_8693"
        ));
    }
}
