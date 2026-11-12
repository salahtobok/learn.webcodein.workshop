package com.webcodein.jobrunr.controller;

import com.webcodein.jobrunr.service.EmailService;
import org.jobrunr.scheduling.JobScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobController.class)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobScheduler jobScheduler;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("POST /api/jobs/enqueue returns 200 OK when valid email is provided")
    void testEnqueueJobSuccess() throws Exception {
        mockMvc.perform(post("/api/jobs/enqueue")
                .param("email", "engineer@domain.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Job enqueued successfully"));
    }

    @Test
    @DisplayName("POST /api/jobs/enqueue returns 400 Bad Request when email parameter is missing")
    void testEnqueueJobMissingEmail() throws Exception {
        mockMvc.perform(post("/api/jobs/enqueue"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Valid email parameter is required"));
    }

    @Test
    @DisplayName("POST /api/jobs/enqueue returns 400 Bad Request when email format is invalid")
    void testEnqueueJobInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/jobs/enqueue")
                .param("email", "invalid-plain-text"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Valid email parameter is required"));
    }

    @Test
    @DisplayName("POST /api/jobs/schedule returns 200 OK and schedules delayed job")
    void testScheduleJobSuccess() throws Exception {
        mockMvc.perform(post("/api/jobs/schedule"))
                .andExpect(status().isOk())
                .andExpect(content().string("Report scheduled in 1 minute"));
    }

    @Test
    @DisplayName("POST /api/jobs/recurring returns 200 OK and registers recurring cron job")
    void testScheduleRecurringJobSuccess() throws Exception {
        mockMvc.perform(post("/api/jobs/recurring"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recurring report job registered successfully"));
    }
}
