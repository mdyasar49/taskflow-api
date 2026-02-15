package com.example.task_management_system.repository;

import com.example.task_management_system.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByStatus(String status, Pageable pageable);

    Page<Task> findAll(Pageable pageable);
}
