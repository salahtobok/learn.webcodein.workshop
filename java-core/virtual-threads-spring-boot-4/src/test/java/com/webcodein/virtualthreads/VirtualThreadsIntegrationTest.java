package com.webcodein.virtualthreads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class VirtualThreadsIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    @DisplayName("Integration: Single report endpoint returns HTTP 200 and Success status")
    void shouldGenerateReportSuccessfully() throws Exception {
        mockMvc.perform(get("/api/reports/generate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Success")))
                .andExpect(jsonPath("$.message", is("Report generated successfully!")));
    }

    @Test
    @DisplayName("Integration: Batch report endpoint spawns tasks on Virtual Threads")
    void shouldExecuteBatchReportsConcurrentlyOnVirtualThreads() throws Exception {
        mockMvc.perform(get("/api/reports/batch").param("count", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].isVirtual", is(true)))
                .andExpect(jsonPath("$[4].isVirtual", is(true)));
    }

    @Test
    @DisplayName("Integration: Lock safety endpoint verifies ReentrantLock unpinning")
    void shouldExecuteWithReentrantLockSafely() throws Exception {
        mockMvc.perform(get("/api/reports/lock-safety"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("Success")))
                .andExpect(jsonPath("$.lockType", is("ReentrantLock")))
                .andExpect(jsonPath("$.pinned", is(false)));
    }

    @Test
    @DisplayName("Integration: Concurrent burst of 20 client requests completes without exhaustion")
    void shouldHandleHighThroughputConcurrentRequests() throws Exception {
        int concurrency = 20;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futures = new ArrayList<>();
            for (int i = 0; i < concurrency; i++) {
                futures.add(executor.submit(() -> {
                    return mockMvc.perform(get("/api/reports/generate"))
                            .andReturn()
                            .getResponse()
                            .getStatus();
                }));
            }

            for (Future<Integer> future : futures) {
                assertThat(future.get()).isEqualTo(200);
            }
        }
    }
}
