package com.example.Task.Manager.Application.service;

import com.example.Task.Manager.Application.dto.TaskCompletionDTO;
import com.example.Task.Manager.Application.entity.User;
import com.example.Task.Manager.Application.exceptionHandler.EditCompletedTaskException;
import com.example.Task.Manager.Application.exceptionHandler.TaskNotFoundException;
import com.example.Task.Manager.Application.exceptionHandler.UncompletedTaskDeletionException;
import com.example.Task.Manager.Application.exceptionHandler.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.Task.Manager.Application.dto.TaskRequestDTO;
import com.example.Task.Manager.Application.dto.TaskResponseDTO;
import com.example.Task.Manager.Application.dto.UpdateTaskDTO;
import com.example.Task.Manager.Application.entity.Task;
import com.example.Task.Manager.Application.repository.TaskRepo;
import com.example.Task.Manager.Application.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepo taskRepo;

    private final UserRepo userRepo;

    private final TaskRedisService taskRedisService;


    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName();

    }

    private TaskResponseDTO mapToDTO(Task task) {

        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getEditedAt(),
                task.isCompleted()
        );

    }

    private Task mapToEntity(TaskRequestDTO taskRequestDTO) {

        Task task = new Task();

        task.setTitle(taskRequestDTO.getTitle());
        task.setDescription(taskRequestDTO.getDescription());
        task.setCreatedAt(LocalDateTime.now());
        task.setCompleted(false);
        task.setEditedAt(null);

        return task;
    }

    public List<TaskResponseDTO> getAllTask() {

        String username = getCurrentUsername();

        List<Task> allTask = taskRepo.findByUserUsername(username);

        return allTask.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public TaskResponseDTO getTaskById(Long id) {

        String username = getCurrentUsername();

        TaskResponseDTO cachedTask = taskRedisService.getTask(id, username);

        if (cachedTask != null) {

            return cachedTask;

        }

        Task task = taskRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(()->new TaskNotFoundException("No task exists with this id"));

        TaskResponseDTO response = mapToDTO(task);

        taskRedisService.saveTask(response, username);

        return response;

    }

    public TaskResponseDTO setTask(TaskRequestDTO taskRequestDTO) {

        String username = getCurrentUsername();

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Task task = mapToEntity(taskRequestDTO);

        task.setUser(user);

        Task saved = taskRepo.save(task);

        return mapToDTO(saved);

    }

    public TaskResponseDTO editTask(Long id, UpdateTaskDTO updateTaskDTO) {

        String username = getCurrentUsername();

        Task task = taskRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new TaskNotFoundException("No task found with this id"));

        if (task.isCompleted()) {

            throw new EditCompletedTaskException("Sorry, this task has been completed, you can't update or edit it");

        }

        task.setTitle(updateTaskDTO.getTitle());
        task.setDescription(updateTaskDTO.getDescription());
        task.setCompleted(updateTaskDTO.isCompleted());
        task.setEditedAt(LocalDateTime.now());

        Task saved = taskRepo.save(task);

        TaskResponseDTO response = mapToDTO(saved);

        taskRedisService.saveTask(response, username);

        return response;

    }

    public TaskResponseDTO updateTaskCompletion(Long id, TaskCompletionDTO taskCompletionDTO) {

        String username = getCurrentUsername();

        Task task = taskRepo.findByIdAndUserUsername(id, username)
                .orElseThrow(() -> new TaskNotFoundException("No task found with this id"));

        task.setCompleted(taskCompletionDTO.isCompleted());

        Task saved = taskRepo.save(task);

        TaskResponseDTO response = mapToDTO(saved);

        taskRedisService.saveTask(response, username);

        return response;

    }

    public void deleteTask(Long id) {

        String username = getCurrentUsername();

       Task task = taskRepo.findByIdAndUserUsername(id, username)
               .orElseThrow(() -> new TaskNotFoundException("No task found with this id"));

       if (!task.isCompleted()) {

           throw new UncompletedTaskDeletionException("Uncompleted Task Can't be Deleted");

       }

       taskRepo.deleteById(id);

       taskRedisService.deleteTask(username, id);

    }

}