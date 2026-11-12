package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.presentation.dto;

import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.Patron;

import java.util.UUID;

public record PatronResponse(
        UUID id,
        String fullName,
        String emailAddress,
        String libraryCardNumber,
        boolean active
) {
    public static PatronResponse from(Patron patron) {
        return new PatronResponse(
                patron.getId(),
                patron.getProfile().fullName(),
                patron.getProfile().emailAddress(),
                patron.getProfile().libraryCardNumber(),
                patron.isActive()
        );
    }
}
