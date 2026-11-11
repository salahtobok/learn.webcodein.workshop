package com.webcodein.taskmanager;

import com.webcodein.taskmanager.application.ProjectApplicationService;
import com.webcodein.taskmanager.application.dto.AddTaskCommand;
import com.webcodein.taskmanager.application.dto.CreateProjectCommand;
import com.webcodein.taskmanager.application.dto.ProjectDto;
import com.webcodein.taskmanager.domain.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("TaskManagerApplication Full Integration Tests")
class TaskManagerApplicationTests {

    @Autowired
    private ProjectApplicationService appService;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @DisplayName("Application context loads successfully")
    void contextLoads() {
        assertNotNull(appService);
        assertNotNull(projectRepository);
    }

    @Test
    @DisplayName("End-to-end project and task lifecycle via Application Service")
    void shouldExecuteFullLifecycle() {
        // 1. Create Project
        UUID projectId = appService.createProject(new CreateProjectCommand("E-Commerce Portal"));
        assertNotNull(projectId);

        // 2. Add Tasks
        UUID task1Id = appService.addTaskToProject(projectId, new AddTaskCommand("Design Cart Schema", 3));
        UUID task2Id = appService.addTaskToProject(projectId, new AddTaskCommand("Implement Checkout Service", 4));
        assertNotNull(task1Id);
        assertNotNull(task2Id);

        // 3. Verify Project state
        ProjectDto dto = appService.getProject(projectId);
        assertEquals("E-Commerce Portal", dto.name());
        assertEquals("ACTIVE", dto.status());
        assertEquals(2, dto.tasks().size());

        // 4. Progress Task 1 and complete it
        appService.startTask(projectId, task1Id);
        appService.markTaskDone(projectId, task1Id);

        // 5. Attempting to complete project while Task 2 is TODO must fail
        assertThrows(IllegalStateException.class, () -> appService.completeProject(projectId));

        // 6. Complete Task 2
        appService.startTask(projectId, task2Id);
        appService.markTaskDone(projectId, task2Id);

        // 7. Complete project now succeeds
        appService.completeProject(projectId);

        // 8. Verify completed status in repository
        ProjectDto completedDto = appService.getProject(projectId);
        assertEquals("COMPLETED", completedDto.status());
    }
}
