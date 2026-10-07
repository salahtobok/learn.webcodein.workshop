package com.webcodein.saas;

import com.webcodein.saas.model.Task;
import com.webcodein.saas.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class SaasApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    private int port;
    
    private RestClient restClient;
    
    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void shouldIsolateTenants() {
        // Create task for Tenant A
        ResponseEntity<Task> responseA = restClient.post()
                .uri("/api/tasks")
                .header("X-Tenant-ID", "tenant-a")
                .body(new Task("Task for A"))
                .retrieve()
                .toEntity(Task.class);
        assertThat(responseA.getStatusCode().is2xxSuccessful()).isTrue();

        // Create task for Tenant B
        ResponseEntity<Task> responseB = restClient.post()
                .uri("/api/tasks")
                .header("X-Tenant-ID", "tenant-b")
                .body(new Task("Task for B"))
                .retrieve()
                .toEntity(Task.class);
        assertThat(responseB.getStatusCode().is2xxSuccessful()).isTrue();

        // Fetch tasks for Tenant A
        List<Task> fetchA = restClient.get()
                .uri("/api/tasks")
                .header("X-Tenant-ID", "tenant-a")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Task>>() {});
        assertThat(fetchA).hasSize(1);
        assertThat(fetchA.get(0).getTitle()).isEqualTo("Task for A");

        // Fetch tasks for Tenant B
        List<Task> fetchB = restClient.get()
                .uri("/api/tasks")
                .header("X-Tenant-ID", "tenant-b")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Task>>() {});
        assertThat(fetchB).hasSize(1);
        assertThat(fetchB.get(0).getTitle()).isEqualTo("Task for B");
    }
}

