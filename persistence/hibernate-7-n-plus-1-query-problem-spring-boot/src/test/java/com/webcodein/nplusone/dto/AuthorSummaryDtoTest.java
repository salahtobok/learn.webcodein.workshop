package com.webcodein.nplusone.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorSummaryDtoTest {

    @Test
    void shouldCreateImmutableDtoRecord() {
        AuthorSummaryDto dto = new AuthorSummaryDto(1L, "Robert C. Martin", "unclebob@cleancoder.com", 5);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Robert C. Martin");
        assertThat(dto.email()).isEqualTo("unclebob@cleancoder.com");
        assertThat(dto.bookCount()).isEqualTo(5);
    }
}
