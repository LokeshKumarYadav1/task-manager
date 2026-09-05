package com.example.Task.Manager.Application.exceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            BadCredentialsException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", "Invalid username or password");
        response.put("Status", 401);

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMethodArguments(MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();

        response.put("Message: ", "Validation Failed");
        response.put("Error: ", errors);
        response.put("Status", 400);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPassword(InvalidPasswordException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 401);

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 404);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> handleExistingUser(UserAlreadyExistException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 409);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaskNotFound(TaskNotFoundException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 404);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(EditCompletedTaskException.class)
    public ResponseEntity<Map<String, Object>> editCompletedTaskException(EditCompletedTaskException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 403);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UncompletedTaskDeletionException.class)
    public ResponseEntity<Map<String, Object>> uncompletedTaskDeletionException(UncompletedTaskDeletionException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 403);

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);

    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAlreadyExistingEmail(EmailAlreadyExistsException ex) {

        Map<String, Object> response = new HashMap<>();

        response.put("Message", ex.getMessage());
        response.put("Status", 409);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);

    }

}
