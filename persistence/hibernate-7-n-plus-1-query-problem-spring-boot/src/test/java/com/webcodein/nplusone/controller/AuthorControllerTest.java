package com.webcodein.nplusone.controller;

import com.webcodein.nplusone.dto.AuthorSummaryDto;
import com.webcodein.nplusone.service.AuthorBookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorBookService authorBookService;

    @Test
    @DisplayName("GET /api/authors/summaries returns HTTP 200 with DTO projections")
    void shouldReturnAuthorSummaries() throws Exception {
        given(authorBookService.getAuthorSummaries()).willReturn(List.of(
                new AuthorSummaryDto(1L, "Author One", "author1@test.com", 3),
                new AuthorSummaryDto(2L, "Author Two", "author2@test.com", 5)
        ));

        mockMvc.perform(get("/api/authors/summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Author One"))
                .andExpect(jsonPath("$[0].bookCount").value(3))
                .andExpect(jsonPath("$[1].name").value("Author Two"))
                .andExpect(jsonPath("$[1].bookCount").value(5));
    }

    @Test
    @DisplayName("GET /api/authors/join-fetch returns HTTP 200 with formatted authors")
    void shouldReturnAuthorsWithJoinFetch() throws Exception {
        given(authorBookService.getAuthorsWithJoinFetch()).willReturn(List.of(
                "Author: Author 1 (Books: 3)",
                "Author: Author 2 (Books: 2)"
        ));

        mockMvc.perform(get("/api/authors/join-fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("Author: Author 1 (Books: 3)"));
    }
}
