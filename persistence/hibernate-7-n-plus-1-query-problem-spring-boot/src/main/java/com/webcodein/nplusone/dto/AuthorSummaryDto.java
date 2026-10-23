package com.webcodein.nplusone.dto;

/**
 * Lightweight DTO Projection to fetch author summary with aggregated book counts
 * without loading full entity graphs into Hibernate's persistence context.
 */
public record AuthorSummaryDto(
    Long id,
    String name,
    String email,
    long bookCount
) {}
