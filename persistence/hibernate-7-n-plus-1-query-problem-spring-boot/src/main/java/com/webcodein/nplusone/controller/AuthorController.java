package com.webcodein.nplusone.controller;

import com.webcodein.nplusone.dto.AuthorSummaryDto;
import com.webcodein.nplusone.service.AuthorBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthorController {

    private final AuthorBookService authorBookService;

    public AuthorController(AuthorBookService authorBookService) {
        this.authorBookService = authorBookService;
    }

    @GetMapping("/authors/unoptimized")
    public ResponseEntity<List<String>> getUnoptimized() {
        return ResponseEntity.ok(authorBookService.getAuthorsWithUnoptimizedLazyLoading());
    }

    @GetMapping("/authors/join-fetch")
    public ResponseEntity<List<String>> getWithJoinFetch() {
        return ResponseEntity.ok(authorBookService.getAuthorsWithJoinFetch());
    }

    @GetMapping("/authors/entity-graph")
    public ResponseEntity<List<String>> getWithEntityGraph() {
        return ResponseEntity.ok(authorBookService.getAuthorsWithEntityGraph());
    }

    @GetMapping("/publishers/batch-fetch")
    public ResponseEntity<List<String>> getWithBatchFetch() {
        return ResponseEntity.ok(authorBookService.getPublishersWithBatchFetching());
    }

    @GetMapping("/authors/summaries")
    public ResponseEntity<List<AuthorSummaryDto>> getAuthorSummaries() {
        return ResponseEntity.ok(authorBookService.getAuthorSummaries());
    }
}
