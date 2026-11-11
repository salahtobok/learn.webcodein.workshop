package com.webcodein.taskmanager.domain.service;

import com.webcodein.taskmanager.domain.model.Priority;
import com.webcodein.taskmanager.domain.model.Project;
import com.webcodein.taskmanager.domain.model.Task;
import com.webcodein.taskmanager.domain.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("TaskAssignmentService Domain Service Tests")
class TaskAssignmentServiceTest {

    private ProjectRepository projectRepository;
    private TaskAssignmentService service;

    @BeforeEach
    void setUp() {
        projectRepository = Mockito.mock(ProjectRepository.class);
        service = new TaskAssignmentService(projectRepository);
    }

    @Test
    @DisplayName("Should assign task to existing project and persist")
    void shouldAssignTaskToProject() {
        UUID projectId = UUID.randomUUID();
        Project project = new Project(projectId, "Cloud Migration");

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        Task task = service.assignTask(projectId, "Setup VPC", Priority.HIGH);

        assertNotNull(task);
        assertEquals("Setup VPC", task.getTitle());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(1, project.getTasks().size());
        verify(projectRepository).save(project);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when project does not exist")
    void shouldThrowWhenProjectNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(projectRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            service.assignTask(nonExistentId, "Orphan Task");
        });
    }
}
