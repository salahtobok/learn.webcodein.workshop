package com.webcodein.workshop.archunit.web;

import java.time.LocalDateTime;

public record CustomerResponse(Long id, String name, LocalDateTime createdAt) {
}
