package com.webcodein.ota.ab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

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

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testSuccessfulUpdateLifecycle() {
        String deviceId = "device-123";

        // 1. Initial State (Slot A is active and successful)
        ResponseEntity<DeviceSlotState> response = restTemplate.getForEntity("/api/devices/" + deviceId + "/state", DeviceSlotState.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getActiveSlot()).isEqualTo("A");
        assertThat(response.getBody().isSuccessfulA()).isTrue();

        // 2. Stage Update
        response = restTemplate.postForEntity("/api/devices/" + deviceId + "/stage-update", Map.of("version", "2.0.0"), DeviceSlotState.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getActiveSlot()).isEqualTo("A");
        assertThat(response.getBody().isBootableB()).isTrue();
        assertThat(response.getBody().isSuccessfulB()).isFalse();

        // 3. Reboot
        response = restTemplate.postForEntity("/api/devices/" + deviceId + "/reboot", null, DeviceSlotState.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getActiveSlot()).isEqualTo("B");
        assertThat(response.getBody().isSuccessfulB()).isFalse();

        // 4. Mark Successful
        response = restTemplate.postForEntity("/api/devices/" + deviceId + "/mark-successful", null, DeviceSlotState.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getActiveSlot()).isEqualTo("B");
        assertThat(response.getBody().isSuccessfulB()).isTrue();
    }

    @Test
    void testFailedUpdateLifecycle() {
        String deviceId = "device-fail";

        // 1. Stage Update to B
        restTemplate.postForEntity("/api/devices/" + deviceId + "/stage-update", Map.of("version", "2.0.0"), DeviceSlotState.class);

        // 2. Reboot switches to B
        ResponseEntity<DeviceSlotState> response = restTemplate.postForEntity("/api/devices/" + deviceId + "/reboot", null, DeviceSlotState.class);
        assertThat(response.getBody().getActiveSlot()).isEqualTo("B");

        // 3. Fallback to A
        response = restTemplate.postForEntity("/api/devices/" + deviceId + "/fallback", null, DeviceSlotState.class);
        assertThat(response.getBody().getActiveSlot()).isEqualTo("A");
        assertThat(response.getBody().isBootableB()).isFalse();
    }
}
