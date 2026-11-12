package com.webcodein.security.zanzibar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import(SecurityConfig.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("GET /api/documents/{id} returns 200 OK when authorized by SpiceDB")
    void getDocument_ReturnsOkWhenAuthorized() throws Exception {
        when(authorizationService.checkPermission(
                eq("document"), eq("doc-100"), eq("read"), eq("user"), eq("alice")))
                .thenReturn(true);

        mockMvc.perform(get("/api/documents/doc-100").with(httpBasic("alice", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("doc-100"))
                .andExpect(jsonPath("$.accessedBy").value("alice"))
                .andExpect(jsonPath("$.content").value("Confidential business roadmap."));
    }

    @Test
    @DisplayName("GET /api/documents/{id} returns 403 Forbidden when SpiceDB denies access")
    void getDocument_ReturnsForbiddenWhenDenied() throws Exception {
        when(authorizationService.checkPermission(
                eq("document"), eq("doc-100"), eq("read"), eq("user"), eq("bob")))
                .thenReturn(false);

        mockMvc.perform(get("/api/documents/doc-100").with(httpBasic("bob", "password")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access Denied"));
    }

    @Test
    @DisplayName("GET /api/documents/{id} returns 401 Unauthorized when unauthenticated")
    void getDocument_ReturnsUnauthorizedWhenNoCredentials() throws Exception {
        mockMvc.perform(get("/api/documents/doc-100"))
                .andExpect(status().isUnauthorized());
    }
}
