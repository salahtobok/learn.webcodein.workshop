package com.webcodein.taskmanager.infrastructure.configuration;

import com.webcodein.taskmanager.domain.repository.ProjectRepository;
import com.webcodein.taskmanager.domain.service.TaskAssignmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public TaskAssignmentService taskAssignmentService(ProjectRepository projectRepository) {
        return new TaskAssignmentService(projectRepository);
    }
}
