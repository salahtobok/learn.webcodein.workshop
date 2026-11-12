package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.infrastructure;

import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.Patron;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.PatronRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPatronRepository implements PatronRepository {

    private final Map<UUID, Patron> database = new ConcurrentHashMap<>();

    @Override
    public Patron save(Patron patron) {
        database.put(patron.getId(), patron);
        return patron;
    }

    @Override
    public Optional<Patron> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public void deleteById(UUID id) {
        database.remove(id);
    }

    public void clear() {
        database.clear();
    }
}
