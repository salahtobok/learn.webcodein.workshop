package com.webcodein.workshop.ota;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OtaUpdateControllerTest {

    @Mock
    private UpdateService updateService;

    @InjectMocks
    private OtaUpdateController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("GET /api/v1/ota/check should return HTTP 200 with patch details when available")
    void shouldReturnHttp200WhenUpdateAvailable() throws Exception {
        UpdateResponse response = new UpdateResponse(
                "2.0.0",
                "https://s3.amazonaws.com/ota-bucket/patches/v2.bsdiff",
                "sha256-hash-999",
                12_500_000L,
                true
        );

        when(updateService.findUpdate("1.0.0", "SensorNodeA")).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/v1/ota/check")
                        .param("currentVersion", "1.0.0")
                        .param("deviceModel", "SensorNodeA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetVersion").value("2.0.0"))
                .andExpect(jsonPath("$.downloadUrl").value("https://s3.amazonaws.com/ota-bucket/patches/v2.bsdiff"))
                .andExpect(jsonPath("$.patchSha256").value("sha256-hash-999"))
                .andExpect(jsonPath("$.sizeBytes").value(12_500_000L))
                .andExpect(jsonPath("$.isCritical").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/ota/check should return HTTP 204 No Content when device is up to date")
    void shouldReturnHttp204WhenNoUpdateAvailable() throws Exception {
        when(updateService.findUpdate("2.0.0", "SensorNodeA")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/ota/check")
                        .param("currentVersion", "2.0.0")
                        .param("deviceModel", "SensorNodeA"))
                .andExpect(status().isNoContent());
    }
}
