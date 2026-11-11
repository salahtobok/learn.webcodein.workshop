package com.webcodein.taskmanager.domain.repository;

import com.webcodein.taskmanager.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
