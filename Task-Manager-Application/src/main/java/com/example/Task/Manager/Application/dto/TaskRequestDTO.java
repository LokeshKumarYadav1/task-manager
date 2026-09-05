package com.example.Task.Manager.Application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "Task title Required")
    private String title;

    @NotBlank(message = "Task description Required")
    private String description;

}
