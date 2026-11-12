package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

import java.util.Optional;
import java.util.UUID;

public interface PatronRepository {
    Patron save(Patron patron);
    Optional<Patron> findById(UUID id);
    void deleteById(UUID id);
}
