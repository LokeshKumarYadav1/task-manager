package com.example.Task.Manager.Application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponseDTO {

    private String username;

    private String email;

    private String message;

}
