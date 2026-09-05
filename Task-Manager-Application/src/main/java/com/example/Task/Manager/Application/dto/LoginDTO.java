package com.example.Task.Manager.Application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "Fill Username")
    private String username;

    @NotBlank(message = "Fill Password")
    private String password;

}
