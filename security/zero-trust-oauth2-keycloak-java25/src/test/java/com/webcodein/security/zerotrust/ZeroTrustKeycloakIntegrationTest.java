package com.webcodein.security.zerotrust;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ZeroTrustKeycloakIntegrationTest {

    @Container
    static GenericContainer<?> keycloak = new GenericContainer<>("quay.io/keycloak/keycloak:26.0.0")
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("realm-export.json"),
                    "/opt/keycloak/data/import/realm-export.json"
            )
            .withCommand("start-dev", "--import-realm")
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/realms/webcodein-realm/.well-known/openid-configuration").forStatusCode(200))
            .withStartupTimeout(Duration.ofMinutes(3));

    @DynamicPropertySource
    static void configureKeycloakProperties(DynamicPropertyRegistry registry) {
        String authServerUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> authServerUrl + "/realms/webcodein-realm");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> authServerUrl + "/realms/webcodein-realm/protocol/openid-connect/certs");
    }

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    @DisplayName("End-to-End: Public endpoint returns HTTP 200 without token")
    void testPublicEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/public/hello"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Keycloak token error [" + response.statusCode() + "]: " + response.body());
        }
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Hello, this is a public endpoint.");
    }

    @Test
    @DisplayName("End-to-End: Secure endpoint returns HTTP 401 Unauthorized without token")
    void testSecureEndpointWithoutToken() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/secure/hello"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("End-to-End: Secure endpoint returns HTTP 200 with real Keycloak token")
    void testSecureEndpointWithKeycloakToken() throws Exception {
        String token = obtainAccessToken("regularuser", "password");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/secure/hello"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Keycloak token error [" + response.statusCode() + "]: " + response.body());
        }
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Hello regularuser, you are authenticated via Zero-Trust OAuth2.");
    }

    @Test
    @DisplayName("End-to-End: Admin dashboard returns HTTP 403 Forbidden for regular user")
    void testAdminDashboardForbiddenForRegularUser() throws Exception {
        String token = obtainAccessToken("regularuser", "password");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/admin/dashboard"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(403);
    }

    @Test
    @DisplayName("End-to-End: Admin dashboard returns HTTP 200 for user with ADMIN role")
    void testAdminDashboardAllowedForAdminUser() throws Exception {
        String token = obtainAccessToken("testuser", "password");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/admin/dashboard"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Keycloak token error [" + response.statusCode() + "]: " + response.body());
        }
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("Welcome to the Admin Dashboard.");
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        String tokenUrl = "http://" + keycloak.getHost() + ":" + keycloak.getMappedPort(8080)
                + "/realms/webcodein-realm/protocol/openid-connect/token";

        String form = "grant_type=password"
                + "&client_id=" + URLEncoder.encode("spring-boot-client", StandardCharsets.UTF_8)
                + "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println("Keycloak token error [" + response.statusCode() + "]: " + response.body());
        }
        assertThat(response.statusCode()).isEqualTo(200);

        String json = response.body();
        int start = json.indexOf("\"access_token\":\"");
        if (start != -1) {
            int tokenStart = start + 16;
            int end = json.indexOf("\"", tokenStart);
            return json.substring(tokenStart, end);
        }
        throw new IllegalStateException("access_token not found in response: " + json);
    }
}
