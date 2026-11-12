package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.presentation;

import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.Patron;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.PatronProfile;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.PatronRepository;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.presentation.dto.PatronResponse;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.presentation.dto.RegisterPatronRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/patrons")
public class PatronController {

    private final PatronRepository repository;

    public PatronController(PatronRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<PatronResponse> registerPatron(@Valid @RequestBody RegisterPatronRequest request) {
        PatronProfile profile = new PatronProfile(
                request.fullName(),
                request.emailAddress(),
                request.libraryCardNumber()
        );
        Patron patron = new Patron(profile);
        Patron saved = repository.save(patron);

        return ResponseEntity.created(URI.create("/api/patrons/" + saved.getId()))
                .body(PatronResponse.from(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatronResponse> getPatron(@PathVariable UUID id) {
        Patron patron = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patron not found with ID: " + id));
        return ResponseEntity.ok(PatronResponse.from(patron));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<PatronResponse> suspendPatron(@PathVariable UUID id) {
        Patron patron = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patron not found with ID: " + id));
        patron.suspendAccount();
        repository.save(patron);
        return ResponseEntity.ok(PatronResponse.from(patron));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<PatronResponse> activatePatron(@PathVariable UUID id) {
        Patron patron = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patron not found with ID: " + id));
        patron.activateAccount();
        repository.save(patron);
        return ResponseEntity.ok(PatronResponse.from(patron));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NoSuchElementException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequest(RuntimeException ex) {
        return Map.of("error", ex.getMessage());
    }
}
