package com.webcodein.taskmanager.application;

import com.webcodein.taskmanager.application.dto.AddTaskCommand;
import com.webcodein.taskmanager.application.dto.CreateProjectCommand;
import com.webcodein.taskmanager.application.dto.ProjectDto;
import com.webcodein.taskmanager.domain.model.Priority;
import com.webcodein.taskmanager.domain.model.Project;
import com.webcodein.taskmanager.domain.model.Task;
import com.webcodein.taskmanager.domain.repository.ProjectRepository;
import com.webcodein.taskmanager.domain.service.TaskAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ProjectApplicationService Application Layer Tests")
class ProjectApplicationServiceTest {

    private ProjectRepository projectRepository;
    private TaskAssignmentService taskAssignmentService;
    private ProjectApplicationService service;

    @BeforeEach
    void setUp() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        taskAssignmentService = Mockito.mock(TaskAssignmentService.class);
        service = new ProjectApplicationService(projectRepository, taskAssignmentService);
    }

    @Test
    @DisplayName("Should create project via command")
    void shouldCreateProject() {
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        UUID id = service.createProject(new CreateProjectCommand("New Product"));

        assertNotNull(id);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Should add task to project via command")
    void shouldAddTask() {
        UUID projectId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Project project = new Project(projectId, "Test Project");
        Task task = project.addTask("Write Specs", Priority.HIGH);

        when(taskAssignmentService.assignTask(eq(projectId), eq("Write Specs"), any(Priority.class)))
                .thenReturn(task);

        UUID returnedId = service.addTaskToProject(projectId, new AddTaskCommand("Write Specs", 3));

        assertEquals(task.getId(), returnedId);
    }

    @Test
    @DisplayName("Should query project and map to DTO")
    void shouldGetProjectDto() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Demo");
        project.addTask("Task 1");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectDto dto = service.getProject(projectId);

        assertEquals(projectId, dto.id());
        assertEquals("Demo", dto.name());
        assertEquals(1, dto.tasks().size());
        assertEquals("Task 1", dto.tasks().get(0).title());
    }

    @Test
    @DisplayName("Should complete project")
    void shouldCompleteProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Finishable");
        Task task = project.addTask("Done Task");
        task.markAsDone();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        service.completeProject(projectId);

        assertEquals("COMPLETED", project.getStatus().name());
        verify(projectRepository).save(project);
    }
}
