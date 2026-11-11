package com.webcodein.taskmanager.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Project Aggregate Root Tests")
class ProjectTest {

    @Test
    @DisplayName("Should create project with ACTIVE status and empty tasks")
    void shouldCreateProjectWithActiveStatus() {
        Project project = new Project("Website Redesign");

        assertNotNull(project.getId());
        assertEquals("Website Redesign", project.getName());
        assertEquals(ProjectStatus.ACTIVE, project.getStatus());
        assertTrue(project.getTasks().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when creating project with null or blank name")
    void shouldThrowExceptionForInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> new Project(null));
        assertThrows(IllegalArgumentException.class, () -> new Project("   "));
    }

    @Test
    @DisplayName("Should add task to project and register domain event")
    void shouldAddTaskToProject() {
        Project project = new Project("Website Redesign");
        Task task = project.addTask("Design Homepage", Priority.HIGH);

        assertEquals(1, project.getTasks().size());
        assertEquals("Design Homepage", task.getTitle());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(1, project.getDomainEvents().size());
    }

    @Test
    @DisplayName("Should reject duplicate task title in same project")
    void shouldRejectDuplicateTaskTitle() {
        Project project = new Project("Website Redesign");
        project.addTask("Design Homepage");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            project.addTask("design homepage"); // Case-insensitive
        });
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Should reject adding task when project is completed")
    void shouldRejectAddingTaskWhenProjectCompleted() {
        Project project = new Project("Website Redesign");
        Task task = project.addTask("Setup CI");
        task.markAsDone();
        project.complete();

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            project.addTask("Another Task");
        });
        assertTrue(ex.getMessage().contains("Cannot add tasks to non-active project"));
    }

    @Test
    @DisplayName("Should enforce maximum 50 tasks limit")
    void shouldEnforceMaxTasksLimit() {
        Project project = new Project("Mega Project");
        for (int i = 0; i < Project.MAX_TASKS_PER_PROJECT; i++) {
            project.addTask("Task " + i);
        }

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            project.addTask("Overflow Task");
        });
        assertTrue(ex.getMessage().contains("reached maximum capacity"));
    }

    @Test
    @DisplayName("Should complete project when all tasks are DONE")
    void shouldCompleteProjectWhenAllTasksDone() {
        Project project = new Project("Sprint 1");
        Task t1 = project.addTask("Task 1");
        Task t2 = project.addTask("Task 2");

        t1.markAsDone();
        t2.markAsDone();

        project.complete();
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        assertEquals(3, project.getDomainEvents().size()); // 2 task added + 1 completed
    }

    @Test
    @DisplayName("Should reject completing project with pending tasks")
    void shouldRejectCompletingProjectWithPendingTasks() {
        Project project = new Project("Sprint 1");
        project.addTask("Task 1");

        IllegalStateException ex = assertThrows(IllegalStateException.class, project::complete);
        assertTrue(ex.getMessage().contains("Cannot complete project with pending or in-progress tasks"));
    }

    @Test
    @DisplayName("Should reconstitute project without triggering validation")
    void shouldReconstituteProject() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        Task task = Task.reconstitute(taskId, "Legacy Task", TaskStatus.DONE, Priority.LOW);

        Project project = Project.reconstitute(id, "Archived Project", ProjectStatus.COMPLETED, List.of(task));

        assertEquals(id, project.getId());
        assertEquals("Archived Project", project.getName());
        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        assertEquals(1, project.getTasks().size());
        assertEquals("Legacy Task", project.getTasks().get(0).getTitle());
    }

    @Test
    @DisplayName("Should return unmodifiable list of tasks")
    void shouldReturnUnmodifiableTaskList() {
        Project project = new Project("Core");
        project.addTask("Initial Task");

        assertThrows(UnsupportedOperationException.class, () -> {
            project.getTasks().clear();
        });
    }
}
