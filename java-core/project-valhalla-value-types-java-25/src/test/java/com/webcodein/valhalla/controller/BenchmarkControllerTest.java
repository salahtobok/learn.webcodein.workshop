package com.webcodein.valhalla.controller;

import com.webcodein.valhalla.entity.BenchmarkRun;
import com.webcodein.valhalla.repository.BenchmarkRunRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BenchmarkController.class)
class BenchmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BenchmarkRunRepository repository;

    @Test
    @DisplayName("GET /api/benchmarks should return list of benchmark runs")
    void shouldReturnAllBenchmarks() throws Exception {
        BenchmarkRun run = new BenchmarkRun("ArrayFlatteningThroughput", 85000L);
        when(repository.findAll()).thenReturn(List.of(run));

        mockMvc.perform(get("/api/benchmarks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("ArrayFlatteningThroughput"))
            .andExpect(jsonPath("$[0].throughput").value(85000));
    }

    @Test
    @DisplayName("POST /api/benchmarks should persist and return created benchmark run")
    void shouldCreateBenchmark() throws Exception {
        BenchmarkRun run = new BenchmarkRun("ScalarReplacementThroughput", 92000L);
        when(repository.save(any(BenchmarkRun.class))).thenReturn(run);

        String json = """
            {
                "name": "ScalarReplacementThroughput",
                "throughput": 92000
            }
            """;

        mockMvc.perform(post("/api/benchmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("ScalarReplacementThroughput"))
            .andExpect(jsonPath("$.throughput").value(92000));
    }
}
