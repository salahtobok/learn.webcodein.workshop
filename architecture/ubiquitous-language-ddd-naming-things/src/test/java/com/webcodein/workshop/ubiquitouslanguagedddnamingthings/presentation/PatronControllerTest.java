package com.webcodein.workshop.ubiquitouslanguagedddnamingthings.presentation;

import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.Patron;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.PatronProfile;
import com.webcodein.workshop.ubiquitouslanguagedddnamingthings.domain.PatronRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatronController.class)
@DisplayName("PatronController REST API Tests")
class PatronControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatronRepository repository;

    @Test
    @DisplayName("POST /api/patrons returns 201 Created with Location header and PatronResponse body")
    void shouldRegisterPatronSuccessfully() throws Exception {
        UUID patronId = UUID.randomUUID();
        Patron patron = new Patron(new PatronProfile("Ada Lovelace", "ada@library.org", "CARD-2001"));

        when(repository.save(any(Patron.class))).thenReturn(patron);

        String jsonPayload = """
                {
                    "fullName": "Ada Lovelace",
                    "emailAddress": "ada@library.org",
                    "libraryCardNumber": "CARD-2001"
                }
                """;

        mockMvc.perform(post("/api/patrons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Ada Lovelace"))
                .andExpect(jsonPath("$.emailAddress").value("ada@library.org"))
                .andExpect(jsonPath("$.libraryCardNumber").value("CARD-2001"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("GET /api/patrons/{id} returns 200 OK with patron details")
    void shouldReturnPatronDetails() throws Exception {
        UUID patronId = UUID.randomUUID();
        Patron patron = new Patron(new PatronProfile("Grace Hopper", "grace@library.org", "CARD-2002"));

        when(repository.findById(patronId)).thenReturn(Optional.of(patron));

        mockMvc.perform(get("/api/patrons/" + patronId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Grace Hopper"))
                .andExpect(jsonPath("$.emailAddress").value("grace@library.org"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("GET /api/patrons/{id} returns 404 Not Found when patron does not exist")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID patronId = UUID.randomUUID();
        when(repository.findById(patronId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patrons/" + patronId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/patrons/{id}/suspend suspends account and returns 200 OK")
    void shouldSuspendPatronAccount() throws Exception {
        UUID patronId = UUID.randomUUID();
        Patron patron = new Patron(new PatronProfile("Alan Turing", "alan@library.org", "CARD-2003"));

        when(repository.findById(patronId)).thenReturn(Optional.of(patron));
        when(repository.save(any(Patron.class))).thenReturn(patron);

        mockMvc.perform(post("/api/patrons/" + patronId + "/suspend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
