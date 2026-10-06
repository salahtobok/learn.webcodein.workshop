package com.webcodein.ota.ab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AbOtaApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/devices";
    }

    @Test
    void testSuccessfulUpdateLifecycle() throws Exception {
        String deviceId = "device-123";

        // 1. Initial State
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/state")).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"activeSlot\":\"A\"");
        assertThat(response.body()).contains("\"successfulA\":true");

        // 2. Stage Update
        request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/stage-update"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"version\":\"2.0.0\"}")).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"activeSlot\":\"A\"");
        assertThat(response.body()).contains("\"bootableB\":true");
        assertThat(response.body()).contains("\"successfulB\":false");

        // 3. Reboot
        request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/reboot"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"activeSlot\":\"B\"");
        assertThat(response.body()).contains("\"successfulB\":false");

        // 4. Mark Successful
        request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/mark-successful"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"activeSlot\":\"B\"");
        assertThat(response.body()).contains("\"successfulB\":true");
    }

    @Test
    void testFailedUpdateLifecycle() throws Exception {
        String deviceId = "device-fail";

        // 1. Stage Update to B
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/stage-update"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"version\":\"2.0.0\"}")).build();
        httpClient.send(request, HttpResponse.BodyHandlers.discarding());

        // 2. Reboot switches to B
        request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/reboot"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.body()).contains("\"activeSlot\":\"B\"");

        // 3. Fallback to A
        request = HttpRequest.newBuilder().uri(URI.create(getBaseUrl() + "/" + deviceId + "/fallback"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.body()).contains("\"activeSlot\":\"A\"");
        assertThat(response.body()).contains("\"bootableB\":false");
    }
}
