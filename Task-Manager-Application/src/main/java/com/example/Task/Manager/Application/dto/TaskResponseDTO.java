package com.example.Task.Manager.Application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TaskResponseDTO {

    private long id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    private boolean completed;

}