package com.example.Task.Manager.Application.service;

import com.example.Task.Manager.Application.dto.LoginDTO;
import com.example.Task.Manager.Application.dto.LoginResponseDTO;
import com.example.Task.Manager.Application.dto.SignupDTO;
import com.example.Task.Manager.Application.dto.SignupResponseDTO;
import com.example.Task.Manager.Application.entity.User;
import com.example.Task.Manager.Application.exceptionHandler.EmailAlreadyExistsException;
import com.example.Task.Manager.Application.exceptionHandler.UserAlreadyExistException;
import com.example.Task.Manager.Application.exceptionHandler.UserNotFoundException;
import com.example.Task.Manager.Application.repository.UserRepo;
import com.example.Task.Manager.Application.security.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public SignupResponseDTO userSignup(SignupDTO signupDTO) {

        if (userRepo.existsByUsername(signupDTO.getUsername())) {

            throw new UserAlreadyExistException("User already exists with this username");

        }

        if (userRepo.existsByEmail(signupDTO.getEmail())) {

            throw new EmailAlreadyExistsException("User already exists with this email");

        }

        User user = new User();

        user.setName(signupDTO.getName());
        user.setUsername(signupDTO.getUsername());
        user.setEmail(signupDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(User.Role.USER);

        userRepo.save(user);

        return new SignupResponseDTO(
                user.getUsername(),
                user.getEmail(),
                "Account Created Successfully"
        );
    }

    public LoginResponseDTO userLogin(LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
        ));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO
                (token, loginDTO.getUsername(), "Logged In Successfully");

    }

    public String deleteUser(Long id) {

        userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user exist with this id"));

        userRepo.deleteById(id);
        
        return "User Removed";

    }

}