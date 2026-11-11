package com.webcodein.taskmanager.infrastructure.persistence;

import com.webcodein.taskmanager.domain.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataProjectRepository extends JpaRepository<Project, UUID> {
}
