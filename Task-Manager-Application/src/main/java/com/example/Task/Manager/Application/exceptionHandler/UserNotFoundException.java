package com.example.Task.Manager.Application.exceptionHandler;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message) {

        super(message);

    }

}
