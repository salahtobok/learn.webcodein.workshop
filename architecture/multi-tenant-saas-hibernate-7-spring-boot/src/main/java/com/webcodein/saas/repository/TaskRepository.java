package com.webcodein.saas.repository;

import com.webcodein.saas.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
