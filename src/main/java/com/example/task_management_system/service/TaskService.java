package com.example.task_management_system.service;

import com.example.task_management_system.model.Task;
import com.example.task_management_system.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "SYSTEM";
    }

    public Page<Task> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByStatus(String status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    public Task createTask(Task task) {
        String currentUser = getCurrentUsername();

        // Use the logged-in user for audits
        task.setCreatedBy(currentUser);
        task.setModifiedBy(currentUser);

        if (task.getStatus() == null || task.getStatus().isEmpty())
            task.setStatus("Open");
        if (task.getPriority() == null || task.getPriority().isEmpty())
            task.setPriority("MEDIUM");

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id).map(task -> {
            if (updatedTask.getTitle() != null)
                task.setTitle(updatedTask.getTitle());
            if (updatedTask.getDescription() != null)
                task.setDescription(updatedTask.getDescription());
            if (updatedTask.getStatus() != null)
                task.setStatus(updatedTask.getStatus());
            if (updatedTask.getPriority() != null)
                task.setPriority(updatedTask.getPriority());
            if (updatedTask.getDueDate() != null)
                task.setDueDate(updatedTask.getDueDate());

            // Always update modifiedBy to the currently logged-in user
            task.setModifiedBy(getCurrentUsername());

            return taskRepository.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
