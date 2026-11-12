package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterPatronRequest(
        @NotBlank String fullName,
        @NotBlank @Email String emailAddress,
        @NotBlank String libraryCardNumber
) {}
