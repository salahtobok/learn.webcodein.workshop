package com.webcodein.security.zanzibar;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final AuthorizationService authorizationService;

    public DocumentController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDocument(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        boolean hasAccess = authorizationService.checkPermission(
                "document", id, "read", "user", username);

        if (!hasAccess) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access Denied"));
        }

        return ResponseEntity.ok(Map.of(
                "id", id,
                "content", "Confidential business roadmap.",
                "accessedBy", username
        ));
    }
}
