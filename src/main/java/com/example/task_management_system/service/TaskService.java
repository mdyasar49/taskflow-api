package com.example.task_management_system.service;

import com.example.task_management_system.model.Task;
import com.example.task_management_system.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByStatus(String status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    public Task createTask(Task task) {
        if (task.getCreatedBy() == null || task.getCreatedBy().isEmpty())
            task.setCreatedBy("SYSTEM");
        if (task.getStatus() == null || task.getStatus().isEmpty()) // Ensure status is set
            task.setStatus("Open");
        if (task.getPriority() == null || task.getPriority().isEmpty())
            task.setPriority("MEDIUM");
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus() != null ? updatedTask.getStatus() : task.getStatus()); // Patch logic
                                                                                                          // safer
            task.setPriority(updatedTask.getPriority() != null ? updatedTask.getPriority() : task.getPriority());
            task.setDueDate(updatedTask.getDueDate());
            task.setModifiedBy("SYSTEM"); // Always update modifier
            // modifiedOn is handled by @PreUpdate in Entity
            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
