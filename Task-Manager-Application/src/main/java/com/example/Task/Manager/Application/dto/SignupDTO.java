package com.example.Task.Manager.Application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupDTO {

    @NotBlank(message = "Name Required")
    private String name;

    @NotBlank(message = "Username Required")
    @Size(min = 5, max = 10)
    private String username;

    @Email
    @NotBlank(message = "Email Required")
    private String email;

    @NotBlank(message = "Password Required")
    @Size(min = 6, max = 15, message = "Password size must be between 6 to 15")
    private String password;

}