package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain;

// GOOD - Clean, professional, domain-focused (Example 6)
public record PatronProfile(
    String fullName,
    String emailAddress,
    String libraryCardNumber
) {
}
