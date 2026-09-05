package com.example.Task.Manager.Application.controller;

import com.example.Task.Manager.Application.dto.TaskCompletionDTO;
import com.example.Task.Manager.Application.dto.TaskRequestDTO;
import com.example.Task.Manager.Application.dto.TaskResponseDTO;
import com.example.Task.Manager.Application.dto.UpdateTaskDTO;
import com.example.Task.Manager.Application.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    ResponseEntity<List<TaskResponseDTO>> getAllTask() {

        List<TaskResponseDTO> tasks = taskService.getAllTask();

        return new ResponseEntity<>(tasks, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {

        TaskResponseDTO task = taskService.getTaskById(id);

        return new ResponseEntity<>(task, HttpStatus.OK);

    }

    @PostMapping
    ResponseEntity<TaskResponseDTO> setTask(@RequestBody @Valid TaskRequestDTO taskRequestDTO) {

        TaskResponseDTO taskResponseDTO = taskService.setTask(taskRequestDTO);

        return new ResponseEntity<>(taskResponseDTO, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @RequestBody @Valid UpdateTaskDTO updateTaskDTO) {

        TaskResponseDTO taskResponseDTO = taskService.editTask(id, updateTaskDTO);

        return new ResponseEntity<>(taskResponseDTO, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();

    }

    @PatchMapping("/{id}")
    ResponseEntity<TaskResponseDTO> updateTaskCompletion(@PathVariable long id, @RequestBody TaskCompletionDTO taskCompletionDTO) {

        TaskResponseDTO taskResponseDTO = taskService.updateTaskCompletion(id, taskCompletionDTO);

        return new ResponseEntity<>(taskResponseDTO, HttpStatus.OK);

    }

}