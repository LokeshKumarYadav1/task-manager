package com.example.Task.Manager.Application.controller;

import com.example.Task.Manager.Application.dto.LoginDTO;
import com.example.Task.Manager.Application.dto.LoginResponseDTO;
import com.example.Task.Manager.Application.dto.SignupDTO;
import com.example.Task.Manager.Application.dto.SignupResponseDTO;
import com.example.Task.Manager.Application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
//@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> userSignup(@Valid @RequestBody SignupDTO signupDTO) {

        return ResponseEntity.ok(userService.userSignup(signupDTO));

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> userLogin(@Valid @RequestBody LoginDTO loginDTO) {

         LoginResponseDTO loginResponseDTO = userService.userLogin(loginDTO);

        return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);

    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        String message = userService.deleteUser(id);

        return new ResponseEntity<>(message, HttpStatus.OK);

    }

}