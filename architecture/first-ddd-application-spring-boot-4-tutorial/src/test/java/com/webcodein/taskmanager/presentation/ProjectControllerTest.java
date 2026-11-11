package com.webcodein.taskmanager.presentation;

import com.webcodein.taskmanager.application.ProjectApplicationService;
import com.webcodein.taskmanager.application.dto.AddTaskCommand;
import com.webcodein.taskmanager.application.dto.CreateProjectCommand;
import com.webcodein.taskmanager.application.dto.ProjectDto;
import com.webcodein.taskmanager.application.dto.TaskDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc
@DisplayName("ProjectController WebMvc Slice Tests")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectApplicationService appService;

    @Test
    @DisplayName("POST /api/projects should return 201 Created")
    void shouldCreateProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        when(appService.createProject(any(CreateProjectCommand.class))).thenReturn(projectId);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Alpha Platform"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/projects/" + projectId))
                .andExpect(jsonPath("$.id").value(projectId.toString()));
    }

    @Test
    @DisplayName("GET /api/projects/{id} should return 200 OK with project data")
    void shouldGetProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        ProjectDto dto = new ProjectDto(
                projectId,
                "Alpha Platform",
                "ACTIVE",
                List.of(new TaskDto(taskId, "Implement Auth", "TODO", 3, "HIGH"))
        );
        when(appService.getProject(projectId)).thenReturn(dto);

        mockMvc.perform(get("/api/projects/" + projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.name").value("Alpha Platform"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.tasks[0].title").value("Implement Auth"));
    }

    @Test
    @DisplayName("GET /api/projects/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID nonExistent = UUID.randomUUID();
        when(appService.getProject(nonExistent))
                .thenThrow(new NoSuchElementException("Project not found: " + nonExistent));

        mockMvc.perform(get("/api/projects/" + nonExistent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("POST /api/projects/{id}/tasks should return 201 Created")
    void shouldAddTask() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(appService.addTaskToProject(eq(projectId), any(AddTaskCommand.class))).thenReturn(taskId);

        mockMvc.perform(post("/api/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Setup Pipeline",
                                  "priorityLevel": 3
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()));
    }

    @Test
    @DisplayName("PUT /api/projects/{id}/complete should return 409 Conflict when tasks unfinished")
    void shouldReturn409WhenCompleteFailsInvariant() throws Exception {
        UUID projectId = UUID.randomUUID();
        doThrow(new IllegalStateException("Cannot complete project with pending tasks"))
                .when(appService).completeProject(projectId);

        mockMvc.perform(put("/api/projects/" + projectId + "/complete"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Cannot complete project with pending tasks"));
    }
}
