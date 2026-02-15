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
        if (task.getCreatedBy() == null)
            task.setCreatedBy("SYSTEM");
        if (task.getModifiedBy() == null)
            task.setModifiedBy("SYSTEM");
        if (task.getPriority() == null)
            task.setPriority("Medium");
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority() != null ? updatedTask.getPriority() : "Medium");
        task.setDueDate(updatedTask.getDueDate());
        task.setModifiedBy(updatedTask.getModifiedBy() != null ? updatedTask.getModifiedBy() : "SYSTEM");
        task.setModifiedOn(java.time.LocalDateTime.now());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
