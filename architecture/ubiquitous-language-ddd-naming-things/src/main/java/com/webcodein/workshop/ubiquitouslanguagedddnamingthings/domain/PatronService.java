package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

// GOOD - Using the Ubiquitous Language strictly and consistently (Example 10)
public class PatronService {

    public void chargeFine(Patron patron) {
        if (patron == null) {
            throw new IllegalArgumentException("Patron cannot be null");
        }
        // Implementation here
    }
}
