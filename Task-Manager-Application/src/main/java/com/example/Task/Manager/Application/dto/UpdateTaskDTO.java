package com.example.Task.Manager.Application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTaskDTO {

    @NotBlank(message = "Title Can't be Empty")
    private String title;

    @NotBlank(message = "Description can't be empty")
    private String description;

    private boolean completed;

}
